# Transaction Signing Project

This project implements a reusable transaction signing flow in an Android application using Jetpack Compose, Kotlin Coroutines, Hilt, and the MVI (Model-View-Intent) architecture.

## About the Application
The application demonstrates a multistep transaction process, specifically a withdrawal flow. The flow initiates from a screen, suspends mid-way to prompt the user with a shared signing interface featuring multiple simulated authentication mechanisms (Passkeys, OTP, EOA), and then resumes to finalize the submission once authentication completes, is canceled, or fails.

---

## Architecture & Signing Coordination

The core challenge of this project is coordinating a mid-flow user authentication step without breaking the business logic encapsulation, leaking UI navigation details, or resorting to complex navigation return event handling. This is achieved using a **suspend-and-resume coordination mechanism** powered by Kotlin Coroutines combined with **centralized event-driven navigation** via Kotlin Channels.

### The Coordination Mechanism

The coordination is managed by a centralized, application-scoped `SigningCoordinator` (exposed via Hilt dependency injection).

```
[WithdrawalViewModel]       [SigningCoordinator]       [MainActivity]       [SigningViewModel]
         |                           |                       |                      |
         |-- (1) getQuotation()      |                       |                      |
         |-- (2) awaitResult(ch,op)->|                       |                      |
         |       [SUSPENDED]         |                       |                      |
         |                           |-- (3) Send request -> |                      |
         |                           |       (Navigation)    |-- (4) Navigate to -> |
         |                           |                       |       SigningScreen  |
         |                           |                       |                      |-- (5) onUiAction(...)
         |                           |                       |                      |
         |                           |<---------------------------------------------|-- (6) sendResult(...)
         |                           |                       |                      |
         |<-------------------------------------------------------------------------|-- (7) NavigateBack Effect
         |                           |                       |                      |
         |<-- (8) [RESUMED] ---------|                       |                      |
         |-- (9) submitSigned...()   |                       |                      |
```

1. **Suspension & Navigation Emission**:
   * The user enters an amount on the Withdrawal screen and taps "Continue".
   * The `WithdrawalViewModel` fetches a transaction quotation containing a challenge string.
   * It calls `signingCoordinator.awaitResult(challenge, OperationType.WITHDRAWAL)`.
   * This immediately suspends the withdrawal flow's execution coroutine, cleanly pausing the transaction mid-flow.
   * Simultaneously, the coordinator posts a `NavigationRequest` to its internal `Channel`.

2. **Centralized Navigation Routing**:
   * The top-level `MainActivity` (or navigation host) collects the coordinator's `navigationRequests` Flow.
   * Upon receiving a request, it pushes the `Route.Signing` route onto the navigation backstack, opening the Signing screen.

3. **Resumption**:
   * The user interacts with the Signing screen (selecting Passkeys, OTP, or EOA) or presses back to cancel.
   * If a signing option is selected, the `SigningViewModel` runs the simulated signing process.
   * Once a result is determined (success, canceled, or failed), the `SigningViewModel` sends the result back to the coordinator using `signingCoordinator.sendResult(challenge, result)`.
   * The Signing screen then triggers navigation to go back.
   * Back on the Withdrawal screen, the suspended coroutine in the `WithdrawalViewModel` resumes execution, receives the `SigningResult`, and finishes submitting the transaction.
