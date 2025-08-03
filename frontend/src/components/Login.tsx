import { Authenticator, Button, useAuthenticator } from "@aws-amplify/ui-react";
import {
  confirmSignIn,
  ConfirmSignInInput,
  signIn,
  SignInInput,
  signUp,
  SignUpInput,
} from "aws-amplify/auth";
import { useState } from "react";

export function LoginWindow(props: { loginComplete: () => void }) {
  const [otp, setOtp] = useState(false);

  return (
    <Authenticator
      key={otp ? "otp-mode" : "password-mode"} // Force re-mount when mode changes
      variation="modal"
      initialState={"signIn"}
      components={{
        SignIn: {
          Footer() {
            const { toForgotPassword } = useAuthenticator();
            return (
              <>
                <Button
                  variation="link"
                  size="small"
                  onClick={() => {
                    setOtp(!otp);
                  }}
                >
                  {otp ? "Sign with Password" : "Sign without Password"}
                </Button>
                <Button
                  variation="link"
                  size="small"
                  onClick={toForgotPassword}
                >
                  Forgot Password
                </Button>
              </>
            );
          },
        },
      }}
      loginMechanisms={["email"]}
      signUpAttributes={["family_name", "given_name"]}
      formFields={{
        signIn: {
          username: {
            placeholder: "Enter Your Email Here",
            isRequired: true,
            label: "Email",
          },
          password: {
            type: otp ? "hidden" : "password",
            label: "Password",
            labelHidden: otp,
            placeholder: "Enter Your Password Here",
            isRequired: !otp,
          },
        },
        signUp: {
          given_name: {
            order: 1,
            placeholder: "Enter Your First Name",
            isRequired: true,
            label: "First Name",
          },
          family_name: {
            order: 2,
            placeholder: "Enter Your Last Name",
            isRequired: true,
            label: "Last Name",
          },
          email: {
            order: 3,
            placeholder: "Enter Your Email",
            isRequired: true,
            label: "Email",
          },
          password: {
            type: "hidden",
            label: "Password",
            labelHidden: true,
          },
          confirm_password: {
            type: "hidden",
            label: "Confirm Password",
            labelHidden: true,
          },
        },
      }}
      services={{
        async handleSignIn(input: SignInInput) {
          const { username, password } = input;

          return signIn({
            username: username,
            password: password,
            options: {
              authFlowType: otp ? "USER_AUTH" : "USER_PASSWORD_AUTH",
              preferredChallenge: "EMAIL_OTP",
            },
          }).then((ret) => {
            if (!otp && ret.isSignedIn) {
              // Username password flow just shows this screen
              props.loginComplete();
            }
            return ret;
          });
        },
        async handleConfirmSignIn(input: ConfirmSignInInput) {
          return confirmSignIn(input).then((ret) => {
            if (otp && ret.isSignedIn) {
              // OTP flow shows OTP code screen
              props.loginComplete();
            }
            return ret;
          });
        },
        async handleSignUp(input: SignUpInput) {
          return signUp({
            username: input.username,
            password: input.password,
            options: {
              ...input.options,
              autoSignIn: {
                authFlowType: "USER_AUTH",
                preferredChallenge: "EMAIL_OTP",
              },
              userAttributes: {
                ...input.options?.userAttributes,
                email: input.username,
              },
            },
          });
        },
      }}
    />
  );
}
