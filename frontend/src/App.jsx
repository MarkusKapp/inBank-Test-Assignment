import { useState } from 'react'
import './App.css'
import apiClient from './api'

function App() {
  const [personalCode, setPersonalCode] = useState('')
  const [loanAmount, setLoanAmount] = useState(2000)
  const [loanPeriod, setLoanPeriod] = useState(12)
  const [decision, setDecision] = useState(null)
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setDecision(null)
    setIsSubmitting(true)

    try {
      const response = await apiClient.post('public/decision', {
        personalCode,
        loanAmount: Number(loanAmount),
        loanPeriod: Number(loanPeriod)
      })
      setDecision(response.data)
    } catch (err) {
      console.error(err)
      if (err.response?.data?.message) {
        // Backend returns a DecisionResponse with a message on error
        setError(err.response.data.message)
      } else {
        setError('Failed to fetch decision. Please try again.')
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="app">
      <div className="shell">
        <header className="hero">
          <h1>Loan Decision</h1>
        </header>

        <section className="panel">
          <form className="form" onSubmit={handleSubmit}>
            <div className="field">
              <label htmlFor="personalCode">Personal Code</label>
              <input
                  id="personalCode"
                  type="text"
                  inputMode="numeric"
                  maxLength={11}
                  value={personalCode}
                  onChange={(e) => {
                    if (/^\d*$/.test(e.target.value)) {
                      setPersonalCode(e.target.value);
                    }
                  }}
                  onBlur={(e) => {
                    if (e.target.value.length === 11) {
                      e.target.setCustomValidity("");
                    } else {
                      e.target.setCustomValidity("Personal code must be exactly 11 digits.");
                    }
                  }}
                  onInput={(e) => e.target.setCustomValidity("")}
                  placeholder="Enter your personal code"
                  required
              />
            </div>

            <div className="field">
              <label htmlFor="loanAmount">Loan Amount (EUR)</label>
              <input
                id="loanAmount"
                type="number"
                value={loanAmount}
                onChange={(e) => setLoanAmount(e.target.value)}
                min="2000"
                max="10000"
                step="100"
                required
              />
            </div>

            <div className="field">
              <label htmlFor="loanPeriod">Loan Period (months)</label>
              <input
                id="loanPeriod"
                type="number"
                value={loanPeriod}
                onChange={(e) => setLoanPeriod(e.target.value)}
                min="12"
                max="60"
                required
              />
            </div>

            <button type="submit" className="primaryButton" disabled={isSubmitting}>
              {isSubmitting ? 'Evaluating...' : 'Get Decision'}
            </button>
          </form>

          {error && (
            <p className="error" role="alert">
              {error}
            </p>
          )}

          {decision && (
            <section
              className={`result ${decision.approved ? 'approved' : 'denied'}`}
              aria-live="polite"
            >
              <h2>{decision.approved ? 'Approved' : 'Denied'}</h2>
              <p>Amount: {decision.approvedAmount} EUR</p>
              <p>Period: {decision.approvedPeriod} months</p>
              {decision.message && <p>{decision.message}</p>}
            </section>
          )}
        </section>
      </div>
    </main>
  )
}

export default App
