import {
  Button,
  Card,
  Divider,
  Flex,
  Heading,
  Text,
  TextField,
  useAuthenticator,
  useTheme,
  View,
} from "@aws-amplify/ui-react";
import {
  fetchUserAttributes,
  FetchUserAttributesOutput,
  signOut,
} from "aws-amplify/auth";
import { useEffect, useState } from "react";
import "@aws-amplify/ui-react/styles.css";
import { getHello } from "../utils/client";

export const Home = (props: { showLoginScreen: () => void }) => {
  const [input, setInput] = useState("");
  const [output, setOutput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { authStatus, user } = useAuthenticator();
  const [attributes, setAttributes] = useState<FetchUserAttributesOutput>({});
  const { tokens } = useTheme();

  const handleServerCall = async () => {
    setIsLoading(true);
    try {
      const res = await getHello({ name: input });
      setOutput(res.message!);
    } catch (err) {
      console.log(err);
      setOutput("Error occurred while contacting server");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    const loadAttributes = async () => {
      try {
        const result: FetchUserAttributesOutput = await fetchUserAttributes();
        setAttributes(result);
      } catch (e) {
        // Unauthenticated
        setAttributes({});
      }
    };
    loadAttributes();
  }, [authStatus, user]);

  return (
    <View
      backgroundColor={tokens.colors.background.secondary}
      minHeight="100vh"
      padding={tokens.space.large}
    >
      <Flex
        direction="column"
        alignItems="center"
        maxWidth="800px"
        margin="0 auto"
      >
        <Card
          variation="elevated"
          padding={tokens.space.xl}
          marginBottom={tokens.space.large}
          width="100%"
        >
          <Flex
            direction="column"
            alignItems="center"
            gap={tokens.space.medium}
          >
            <Heading level={1} color={tokens.colors.font.primary}>
              Tetris Template
            </Heading>

            {attributes.given_name && (
              <Text
                fontSize={tokens.fontSizes.large}
                color={tokens.colors.font.secondary}
              >
                Welcome back, {attributes.given_name} {attributes.family_name}!
              </Text>
            )}

            <Flex
              direction="row"
              gap={tokens.space.small}
              marginTop={tokens.space.medium}
            >
              {authStatus === "authenticated" ? (
                <Button
                  variation="primary"
                  colorTheme="error"
                  onClick={async () => {
                    await signOut();
                  }}
                >
                  Sign Out
                </Button>
              ) : (
                <Button
                  variation="primary"
                  onClick={() => props.showLoginScreen()}
                >
                  Sign In
                </Button>
              )}
            </Flex>
          </Flex>
        </Card>

        <Card variation="elevated" padding={tokens.space.xl} width="100%">
          <Flex direction="column" gap={tokens.space.medium}>
            <Heading level={3} color={tokens.colors.font.primary}>
              Server Communication
            </Heading>

            <Divider />

            <View>
              <Text
                fontSize={tokens.fontSizes.medium}
                fontWeight={tokens.fontWeights.semibold}
                marginBottom={tokens.space.small}
              >
                Send Message to Server:
              </Text>
              <TextField
                label=""
                placeholder="Enter your message here..."
                value={input}
                onChange={(event) => setInput(event.target.value)}
                width="100%"
              />
            </View>

            <Button
              variation="primary"
              onClick={handleServerCall}
              isDisabled={!input.trim() || isLoading}
              isLoading={isLoading}
              loadingText="Sending..."
            >
              Send Message
            </Button>

            {output && (
              <View>
                <Text
                  fontSize={tokens.fontSizes.medium}
                  fontWeight={tokens.fontWeights.semibold}
                  marginBottom={tokens.space.small}
                >
                  Server Response:
                </Text>
                <Card
                  variation="outlined"
                  backgroundColor={tokens.colors.background.tertiary}
                  padding={tokens.space.medium}
                >
                  <Text fontSize={tokens.fontSizes.medium}>{output}</Text>
                </Card>
              </View>
            )}
          </Flex>
        </Card>
      </Flex>
    </View>
  );
};
