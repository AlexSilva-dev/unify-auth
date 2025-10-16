# Use Case: Login and Register with Google

**ID:** UC-03

**Actors:**
*   **Primary:** User (a developer consuming the API on behalf of an end-user)
*   **Secondary:** System (the Unify Auth API itself)
*   **Tertiary:** Google Authentication Service

**Description:**
This use case describes how a user can register or log in using their Google account. The system accepts either an `id_token` (from native clients) or an `authorization_code` (from web clients), validates the credential with Google, creates a new account if necessary, and returns internal authentication tokens.

---

**Preconditions:**
1.  The client application has obtained either an `id_token` or an `authorization_code` from Google.
2.  The system is operational and has network access to Google's servers.

**Postconditions (on success):**
1.  If the user is new, a `User` account and a `Credential` record are created.
2.  The user is considered authenticated in the system.
3.  The system returns a new `accessToken` and a new `refreshToken` to the client.

---

## Main Flow (Happy Path)

1.  The **User**'s client application sends a `POST` request to the `/api/auth/login/google` endpoint. The request body contains either the `id_token` or the `authorization_code` and `redirect_uri`.
2.  The **System** receives the request and inspects the body.
3.  **If `authorization_code` is present:**
    a. The **System** makes a secure server-to-server request to the **Google Authentication Service**, exchanging the `authorization_code` for an `id_token`.
    b. The flow proceeds to step 4 using the obtained `id_token`.
4.  **If `id_token` is present (either from the client or from step 3a):**
    a. The **System** communicates with the **Google Authentication Service** to validate the `id_token`.
    b. If valid, Google returns the user's profile information (email, name, Google User ID).
5.  The **System** searches its database for a `Credential` with a `provider` of 'GOOGLE' and a `providerId` matching the Google User ID.
6.  **If a credential is found (Login):** The system proceeds to step 8 (token generation).
7.  **If no credential is found (First-time login/Sign-up):**
    a. The system searches for an existing `User` with the email returned by Google.
    b. If no user exists with that email, a new `User` account is created.
    c. A new `Credential` record is created, linking the Google User ID to the user account (new or existing).
8.  The **System** generates a new `accessToken` and `refreshToken` pair for the authenticated user.
9.  The **System** returns a `200 OK` (for login) or `201 Created` (for a new user) response containing the `accessToken` and `refreshToken`.

## Alternative Flows

### A1: Invalid Google Credential

*   **Trigger:** In step 3a or 4a, Google reports that the `authorization_code` or `id_token` is invalid or expired.
1.  The **System** stops the process.
2.  The **System** returns a `401 Unauthorized` error response.
3.  The use case ends.

### A2: Google Service Unreachable

*   **Trigger:** The system cannot connect to Google's authentication servers.
1.  The **System** stops the process.
2.  The **System** returns a `503 Service Unavailable` error response.
3.  The use case ends.
