# Unify Auth

## What is it?

Unify Auth is a centralized authentication platform that functions as a backend, designed to unify social login (and other methods) across multiplatform applications.

The client application interacts with Unify Auth via API, allowing the construction of fully native login and registration flows without the need for redirects or WebViews, prioritizing the best user experience.

## Main Features

- **Native Integration (API-First)**: Allows client applications to build their own user interfaces and communicate with Unify Auth via API, ensuring a 100% native experience.
- **Centralized Authentication**: A single point to manage your users, regardless of the login method (social, email/password, etc.).
- **Security with JWT**: Uses the JSON Web Tokens standard with a `refresh token` mechanism for persistent and secure sessions.
- **Offline Validation**: Allows product backends to validate tokens quickly and securely without the need for constant network calls to the authentication server.
- **Admin Interface**: Includes a WebAssembly (WASM) admin interface to manage platform settings.

## Authentication Providers

### Initial Support
- [x] Google (Social Login)
- [x] Email and Password

### Planned for the Future
- [ ] Apple
- [ ] Microsoft
- [ ] GitHub

## Architecture and Authentication Flow

The interaction flow prioritizes the native experience of the client application:

1.  **Native Credential Collection**: The client application (native Android/iOS, Web, etc.) collects the user's credentials. This can be an email/password, or an **authorization token/code** from a social login provider (like Google, Apple, etc.) obtained through the provider's native SDK.

2.  **Client -> Unify Auth**: The client application sends these credentials (e.g., the Google token) to a specific endpoint on the Unify Auth backend.

3.  **Unify Auth -> Client**: Unify Auth validates the received credentials (for example, by verifying the token's validity with Google). If authentication is successful, it finds or creates the user in its database and generates its own tokens (`access_token` and `refresh_token`), returning them to the client application.

4.  **Client -> Product Backend**: The client application stores the Unify Auth tokens securely. For each request that requires authentication, the client sends the `access_token` in the `Authorization` header.

5.  **Product Backend**: Your product's backend **validates the token offline**, using the public key provided by Unify Auth. If the token is valid, the backend processes the request.

6.  **Token Renewal**: When the `access_token` expires, the client application uses the `refresh_token` to silently request a new pair of tokens from Unify Auth.

## Initial Setup

To configure Unify Auth for execution, follow the steps below:

1.  **Configure the Database**:
    -   You will need a database instance to store user data and Unify Auth settings.
    -   **Important**: It is strongly recommended to use a separate database and not the same one as your main application to avoid conflicts and maintain separation of concerns.
    -   Configure the database connection credentials in the environment file of the server module (e.g., `server/.env` or `local.properties`).

2.  **Administrator Credentials**:
    -   Define the initial administrator user and password through the following environment variables:
        -   `USER_ADMIN`
        -   `PASSWORD_ADMIN`

## Admin Interface (WASM)

Unify Auth uses a WebAssembly (WASM) application as a control panel to modify the platform's internal settings.

-   This interface allows for visual management of the platform.
-   To access it, use the administrator credentials defined in the environment variables.
