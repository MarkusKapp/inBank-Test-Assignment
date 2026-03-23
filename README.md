# Loan Decision Engine

## Description
This project is a decision engine which takes in a personal identification code, loan amount, and loan period in months. It returns a decision (negative or positive) and the approved amount.

The application consists of a Spring Boot backend and a React frontend.

## Prerequisites
- Java 21 or later
- Node.js (v18 or later recommended)
- npm or yarn

## Getting Started

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

### Running Tests

To run the backend tests:

**Linux/macOS/Windows:**
```bash
./gradlew test
```

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
