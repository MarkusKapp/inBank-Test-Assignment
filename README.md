# Loan Decision Engine

## Description
This project is a decision engine which takes in a personal identification code, loan amount, and loan period in months. It returns a decision (negative or positive) and the approved amount.

The application consists of a Spring Boot backend and a React frontend.


## Getting Started

#### Clone the repository to your local machine.

### Backend Setup

1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```

2. Build and run the application using Gradle wrapper:

   ```
   ./gradlew bootRun
   ```

   **Note:** The command will stay running (showing ~80% execution in Gradle) to keep the server active. This is expected and you should not close this terminal. Open a new terminal for other commands.

   **Troubleshooting:**
   If you encounter the error `ERROR: JAVA_HOME is not set`, it means your terminal cannot locate your Java installation.
   
   **Windows PowerShell Temporary Fix:**
   Run the following command (adjusting the path to your JDK version) before `bootRun`:
   ```powershell
   # Example for IntelliJ-installed JDK:
   $env:JAVA_HOME = "$env:USERPROFILE\.jdks\ms-21.0.8"
   ./gradlew bootRun
   ```

### Frontend Setup

1. Open a new terminal and navigate to the `frontend` directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

   The frontend application will likely start on `http://localhost:5173` (check the terminal output for the exact URL).

   **Note:** If port 5173 is in use, Vite will automatically use the next available port (e.g., 5174). The README assumes the default, but check your terminal.

## Usage

1. Open your browser and go to the frontend URL (e.g., `http://localhost:5173`).
2. Enter the personal code, loan amount, and loan period.
3. Submit the form to see the decision.

## Specific Scenarios (Mock Data)

The following personal codes are hardcoded for testing purposes:

- `49002010965` - Debt (Decision: Negative)
- `49002010976` - Segment 1 (Credit Modifier: 100)
- `49002010987` - Segment 2 (Credit Modifier: 300)
- `49002010998` - Segment 3 (Credit Modifier: 1000)

## Constraints

- Minimum loan amount: 2000 €
- Maximum loan amount: 10000 €
- Minimum loan period: 12 months
- Maximum loan period: 60 months

## Project Decisions & Thought Process

### Technology Stack
- **Backend:** I chose Spring Boot for the backend because of its ease of setting up RESTful APIs, and strong support for validation and exception handling.
- **Frontend:** I've had more experience with Vue in the past, but chose React for this project to diversify my skill set and learn more about different frontend frameworks.
- **Build Tools:** Used Gradle for the backend to manage dependencies and build tasks, and Vite for the frontend.


### Architecture & Design
I followed a standard layered architecture to separate concerns and ensure maintainability:
- **Controller Layer (`DecisionController`):** Handles incoming HTTP requests and responses. It relies on DTOs (`DecisionRequest`, `DecisionResponse`) to structure data transfer and enforce validation rules.
- **Service Layer (`DecisionService`):** Contains the core business logic. This is where the credit score calculation and decision-making happen.
- **Exception Handling:** Implemented a global exception handler (`GlobalExceptionHandler`) using `@RestControllerAdvice`. This ensures that validation errors (e.g., invalid personal code format) or business exceptions are caught and returned to the frontend in a consistent, user-friendly JSON format, rather than exposing raw stack traces.
- 

### Key Implementation Details
1.  **Validation:** Input validation is handled using Validation annotations (`@NotNull`, `@Min`, `@Max`, `@Pattern`) directly on the request DTO along with `@Valid` on the controller method parameter. This ensures invalid data is rejected early. These checks are made in the frontend as well, but as these can be passed the main validation logic is in the backend.
2.  **Decision Logic:** The task was not quite clear on some edge case scenarios on what the decision engine should do so in this example where:
   - CreditModifier is 100
   - Loan amount is 3000
   - Loan period is 12 months

whether it should approve the loan at 2000 with a period of 20 months or try to match the loan amount at 3000 and 30 months.

I implemented the logic to try to match the loan amount first, and if it cannot be matched, then try to match the loan period. If neither can be matched, then the loan is rejected. This approach prioritizes meeting the customer's requested amount while still trying to find a suitable period if the amount cannot be met.

3.  **Frontend Integration:** The frontend uses `axios` for API calls. I implemented dynamic error handling to display backend validation messages directly to the user (e.g., "Personal code must constitute of 11 digits").
4.  **Loan Constraints:** Created a separate `LoanConstraints` class to centralize the minimum and maximum values for loan amount and period. This makes it easier to maintain and update these constraints in the future without having to search through the codebase.
5.  **Logging:** Added logging statements in the service layer and exception handling to trace the decision-making process and debug any issues that arise during execution.

### Testing
- **Unit Tests:** Focused on `DecisionService` to verify all branching logic (approval, rejection, finding new periods/amounts).
- **Integration Tests:** `DecisionControllerIntegrationTest` ensures the API endpoints work correctly with the service layer and validation rules.


### What is one thing you would improve about the take home assignment and how would you improve it?
- Making the task more specific on the expected behavior of the decision engine in edge cases.
- Providing more detail on whether the focus is more on backend logic, frontend implementation, or both.