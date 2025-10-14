# Technical Documentation - Unify Auth

Welcome to the Unify Auth technical documentation.

This document serves as the source of truth for the development and consumption of the API. It is organized around the platform's use cases and non-functional requirements.

## Use Cases

Below are the main supported and planned use cases for the API. Each link will lead to a page with detailed technical specifications.

*   **Core Authentication**
    *   [UC-01: Register with Email and Password](./use-cases/01-register-with-email-and-password.md)
    *   *UC-02: Login with Email and Password (coming soon)*
    *   *UC-03: Login with Google (coming soon)*
    *   *UC-04: Refresh Access Token (coming soon)*

*   **Provider Expansion**
    *   *UC-05: Login with Apple (coming soon)*
    *   *UC-06: Login with Microsoft (coming soon)*
    *   *UC-07: Login with GitHub (coming soon)*

*   **User Management**
    *   *UC-08: Get User Profile (coming soon)*

## Platform Enhancements (Non-Functional Requirements)

In addition to the use cases, the following security and scalability enhancements are planned. They represent qualities and constraints that apply to multiple use cases.

*   **Bot Protection:** Implement a mechanism like reCAPTCHA or hCaptcha in the authentication flows.
*   **Rate Limiting:** Add request limit controls on sensitive endpoints.
