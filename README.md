# Transaction Signing Project

This project implements a reusable transaction signing flow in an Android application using Jetpack Compose, Kotlin Coroutines, Hilt, and the MVI (Model-View-Intent) architecture.

## About the Application
The application demonstrates a multistep transaction process, specifically a withdrawal flow. The flow initiates from a screen, suspends mid-way to prompt the user with a shared signing interface featuring multiple simulated authentication mechanisms (Passkeys, OTP, EOA), and then resumes to finalize the submission once authentication completes, is canceled, or fails.

---

## Architecture & Signing Coordination

The core challenge of this project is coordinating a mid-flow user authentication step without breaking the business logic encapsulation or resorting to complex navigation return event handling. This is achieved using a **suspend-and-resume coordination mechanism** powered by Kotlin Coroutines.

### The Coordination Mechanism

The coordination is managed by a centralized, application-scoped `SigningCoordinator` (exposed via Hilt dependency injection).

```
[WithdrawalViewModel]                           [SigningCoordinator]                    [SigningViewModel]
         |                                                |                                      |
         |-- (1) getQuotation()                           |                                      |
         |-- (2) NavigateToSigning Effect ------------------------------------------------------>|
         |-- (3) awaitResult(challenge) ----------------->|                                      |
         |       [COROUTINE SUSPENDED]                    |                                      |
         |                                                |                                      |
         |                                                |                                      |-- (4) onUiAction(Sign/Back)
         |                                                |                                      |-- (5) sendResult(challenge, result)
         |                                                |<-------------------------------------|
         |                                                |                                      |
         |<--------------------------------------------------------------------------------------|-- (6) NavigateBack Effect
         |                                                |                                      |
         |<-- (7) [COROUTINE RESUMED] --------------------|                                      |
         |                                                |                                      |
         |-- (8) submitSignedTransaction()                |                                      |
```

1. **Suspension**:
   * The user enters an amount on the Withdrawal screen and taps "Continue".
   * The `WithdrawalViewModel` fetches a transaction quotation containing a challenge string.
   * Once received, it fires a navigation effect to show the Signing screen and immediately calls `signingCoordinator.awaitResult(challenge)`. 
   * This suspends the withdrawal flow's execution coroutine, cleanly pausing the transaction mid-flow.

2. **Resumption & Navigation**:
   * The user interacts with the Signing screen (selecting Passkeys, OTP, or EOA) or presses back to cancel.
   * If a signing option is selected, the `SigningViewModel` runs the simulated signing process (with a 1.5s delay).
   * Once a result is determined (signed, canceled, or if the initialization fails—such as when fetching signing methods fails), the `SigningViewModel` completes the transaction signature result by calling `signingCoordinator.sendResult(challenge, result)`.
   * Immediately after sending the result, the Signing screen triggers navigation to go back to the previous screen by popping the navigation backstack.
   * Back on the Withdrawal screen, the suspended coroutine in the `WithdrawalViewModel` resumes execution, handling the returned `SigningResult` and submitting the transaction.
   * In case of success, a success screen is displayed; in case of an error or cancellation, an error toast is shown.

---

## Technical Trade-offs

### 1. Separation of Concerns & Reusability
* **Our Approach**: The entire signing UI, logic, and simulation are encapsulated inside the `:feature:signing` module. Other feature modules (like `:feature:withdrawal` or future transfers and swaps) do not need to know how signing is performed. They only reuse the public `SigningCoordinator` API and navigate to the signing route.
* **Benefit**: High modularity and zero leak of signing implementation details into other feature modules.
* **Trade-off**: The calling module requires two integrations: invoking the `SigningCoordinator` and navigating to the Signing screen.

### 2. Linearity vs. Global Broadcasting (SharedFlow)
* **Alternative Considered**: Using a global `SharedFlow` to broadcast signing results across the app.
* **Trade-off**: While this decouples view models, we lose **linearity** and the ability to suspend/resume in the exact same flow of running things. Using a `SharedFlow` forces the originating ViewModel to register listeners, manage subscription lifecycles, and handle state reconciliation across separate coroutine scopes. Our coroutine suspension approach lets us write asynchronous steps as if they were simple, synchronous code blocks.

### 3. Suspension vs. Navigation Results API
* **Alternative Considered**: Navigating to the signing screen and using the navigation backstack's saved state handle (e.g. NavResult) to pass the signature back to the Withdrawal screen.
* **Trade-off**: This approach breaks linearity because the `WithdrawalViewModel` must terminate its initial flow execution when navigating away and then rebuild the state machine once the result is received via navigation hooks. By using the coordinator suspension pattern, the flow maintains its local variables, execution state, and scope seamlessly without needing to tear down and reconstruct the state mid-way.
