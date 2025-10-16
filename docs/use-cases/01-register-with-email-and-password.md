# Use Case: Register with Email and Password

**ID:** UC-01

**Actors:**
*   **Primary:** User (a developer consuming the API on behalf of an end-user)
*   **Secondary:** System (the Unify Auth API itself)

**Description:**
This use case describes the process of a new user registering on the platform by providing a name, email, and password. The system validates the data, creates the account, and returns authentication tokens.

---

**Preconditions:**
1.  The provided email must not be previously registered in the system.
2.  The system must be operational.

**Postconditions (on success):**
1.  A new user account is created and persisted in the database.
2.  The user's password is a stored securely (hashed) in a separate credentials table.
3.  The user is considered authenticated in the new session.
4.  The system returns an `accessToken` and a `refreshToken` to the client.

---

## Main Flow (Happy Path)

1.  The **User** sends a `POST` request to the `/api/auth/register/email` endpoint, containing `name`, `email`, and `password` in the request body.
2.  The **System** receives the request and validates the input data:
    *   `name` is not empty and has at least 3 characters.
    *   `email` has a valid format.
    *   `password` meets the minimum security criteria.
3.  The **System** checks the database to see if a user with the provided `email` already exists.
4.  The **System** generates a secure hash for the `password`.
5.  The **System** persists the new user account and the associated credentials with the hashed password in a single, atomic transaction. If any step fails, the entire operation is rolled back.
6.  The **System** generates a new `accessToken` and `refreshToken` pair associated with the new user.
7.  The **System** returns a `201 Created` response containing the `accessToken` and `refreshToken`.

## Alternative Flows

### A1: Invalid Input Data

*   **Trigger:** In step 2 of the Main Flow, any of the input data (`name`, `email`, `password`) fails validation.
1.  The **System** stops the process.
2.  The **System** returns a `400 Bad Request` error response, indicating which field is invalid.
3.  The use case ends.

### A2: User Already Registered

*   **Trigger:** In step 3 of the Main Flow, the system finds an existing user with the same email.
1.  The **System** stops the process.
2.  The **System** returns a `409 Conflict` error response, indicating that the email is already in use.
3.  The use case ends.
