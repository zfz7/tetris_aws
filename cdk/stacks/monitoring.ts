import { App, Duration, Stack, StackProps } from 'aws-cdk-lib';
import { Dashboard, GraphWidget, GraphWidgetView, Metric, TextWidget } from 'aws-cdk-lib/aws-cloudwatch';
import { Operation } from '../bin/types';

interface MonitoringStackProps extends StackProps {
  dashboardName: string;
  apiName: string;
  region: string;
  stage: string;
  operations: Operation[];
  ddbTables: string[];
}

export class MonitoringStack extends Stack {
  constructor(scope: App, id: string, props: MonitoringStackProps) {
    super(scope, id, props);

    const dashboard = new Dashboard(this, props.dashboardName, {
      dashboardName: props.dashboardName,
    });

    // Add overall widgets
    this.addOverallWidgets(dashboard, props);

    // Add ddb widgets
    props.ddbTables.forEach((ddbTableName) => {
      this.addDdbWidgets(dashboard, ddbTableName, props);
    });

    // Add individual operation widgets
    props.operations.forEach((operation) => {
      this.addOperationWidgets(dashboard, operation, props);
    });
  }

  private addOverallWidgets(dashboard: Dashboard, props: MonitoringStackProps) {
    const overallWidgets = [
      new TextWidget({ markdown: '## Overall Metrics', width: 24, height: 1 }),
      this.createLatencyGraph('Overall Latency', props.apiName, props.region, props.stage),
      this.createCountGraph(`Overall Count`, props.apiName, props.region, props.stage),
      this.create4xxGraph('Overall 4xx', props.apiName, props.region, props.stage),
      this.create5xxGraph('Overall 5xx', props.apiName, props.region, props.stage),
    ];
    dashboard.addWidgets(...overallWidgets);
  }

  private addDdbWidgets(dashboard: Dashboard, tableName: string, props: MonitoringStackProps) {
    const ddbWidgets = [
      new TextWidget({ markdown: `## DDB Table: ${tableName}`, width: 24, height: 1 }),
      this.createDdbGraph(`DDB Table: ${tableName}`, tableName, props.region),
    ];
    dashboard.addWidgets(...ddbWidgets);
  }

  private addOperationWidgets(dashboard: Dashboard, operation: Operation, props: MonitoringStackProps) {
    const operationWidget = [
      new TextWidget({ markdown: `### ${operation.name} | ${operation.path}`, width: 24, height: 1 }),
      this.createLatencyGraph(
        `${operation.name} Latency`,
        props.apiName,
        props.region,
        props.stage,
        operation.method,
        operation.path,
      ),
      this.createCountGraph(
        `${operation.name} Count`,
        props.apiName,
        props.region,
        props.stage,
        operation.method,
        operation.path,
      ),
      this.create4xxGraph(
        `${operation.name} 4xx`,
        props.apiName,
        props.region,
        props.stage,
        operation.method,
        operation.path,
      ),
      this.create5xxGraph(
        `${operation.name} 5xx`,
        props.apiName,
        props.region,
        props.stage,
        operation.method,
        operation.path,
      ),
    ];
    dashboard.addWidgets(...operationWidget);
  }

  private createLatencyGraph(
    title: string,
    apiName: string,
    region: string,
    stage: string,
    method: string | undefined = undefined,
    resource: string | undefined = undefined,
  ): GraphWidget {
    const dimensionsMap: Record<string, string> = {
      ApiName: apiName,
      Stage: stage,
    };
    if (method) dimensionsMap.Method = method.toUpperCase();
    if (resource) dimensionsMap.Resource = resource;
    return new GraphWidget({
      title: title,
      left: ['P99', 'P95', 'P90', 'P50'].map(
        (metric) =>
          new Metric({
            namespace: 'AWS/ApiGateway',
            metricName: 'Latency',
            dimensionsMap: dimensionsMap,
            region: region,
            statistic: metric,
            period: Duration.seconds(60),
          }),
      ),
      view: GraphWidgetView.TIME_SERIES,
      stacked: true,
      region: region,
      width: 8,
    });
  }

  private createCountGraph(
    title: string,
    apiName: string,
    region: string,
    stage: string,
    method: string | undefined = undefined,
    resource: string | undefined = undefined,
  ): GraphWidget {
    const dimensionsMap: Record<string, string> = {
      ApiName: apiName,
      Stage: stage,
    };
    if (method) dimensionsMap.Method = method.toUpperCase();
    if (resource) dimensionsMap.Resource = resource;
    return new GraphWidget({
      title: title,
      left: [
        new Metric({
          namespace: 'AWS/ApiGateway',
          metricName: 'Count',
          dimensionsMap: dimensionsMap,
          region: region,
          statistic: 'Sum',
          period: Duration.seconds(60),
        }),
      ],
      view: GraphWidgetView.TIME_SERIES,
      stacked: true,
      region: region,
      width: 6,
    });
  }

  private create4xxGraph(
    title: string,
    apiName: string,
    region: string,
    stage: string,
    method: string | undefined = undefined,
    resource: string | undefined = undefined,
  ): GraphWidget {
    const dimensionsMap: Record<string, string> = {
      ApiName: apiName,
      Stage: stage,
    };
    if (method) dimensionsMap.Method = method.toUpperCase();
    if (resource) dimensionsMap.Resource = resource;
    return new GraphWidget({
      title: title,
      left: [
        new Metric({
          namespace: 'AWS/ApiGateway',
          metricName: '4XXError',
          dimensionsMap: dimensionsMap,
          region: region,
          statistic: 'Sum',
          period: Duration.seconds(60),
        }),
      ],
      view: GraphWidgetView.TIME_SERIES,
      stacked: true,
      region: region,
      width: 5,
    });
  }

  private create5xxGraph(
    title: string,
    apiName: string,
    region: string,
    stage: string,
    method: string | undefined = undefined,
    resource: string | undefined = undefined,
  ): GraphWidget {
    const dimensionsMap: Record<string, string> = {
      ApiName: apiName,
      Stage: stage,
    };
    if (method) dimensionsMap.Method = method.toUpperCase();
    if (resource) dimensionsMap.Resource = resource;
    return new GraphWidget({
      title: title,
      left: [
        new Metric({
          namespace: 'AWS/ApiGateway',
          metricName: '5XXError',
          dimensionsMap: {
            ApiName: apiName,
            Stage: stage,
          },
          region: region,
          statistic: 'Sum',
          period: Duration.seconds(60),
        }),
      ],
      view: GraphWidgetView.TIME_SERIES,
      stacked: true,
      region: region,
      width: 5,
    });
  }

  private createDdbGraph(title: string, tableName: string, region: string): GraphWidget {
    return new GraphWidget({
      title: title,
      left: [
        new Metric({
          namespace: 'AWS/DynamoDB',
          metricName: 'ConsumedReadCapacityUnits',
          dimensionsMap: {
            TableName: tableName,
          },
          region: region,
          statistic: 'Average',
          period: Duration.seconds(60),
        }),
      ],
      right: [
        new Metric({
          namespace: 'AWS/DynamoDB',
          metricName: 'ConsumedWriteCapacityUnits',
          dimensionsMap: {
            TableName: tableName,
          },
          region: region,
          statistic: 'Average',
          period: Duration.seconds(60),
        }),
      ],
      view: GraphWidgetView.TIME_SERIES,
      stacked: true,
      region: region,
      width: 24,
    });
  }
}
