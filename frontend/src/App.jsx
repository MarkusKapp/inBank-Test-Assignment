import { useState } from 'react'
import './App.css'
import apiClient from './api'

function App() {
  const [personalCode, setPersonalCode] = useState('')
  const [loanAmount, setLoanAmount] = useState(2000)
  const [loanPeriod, setLoanPeriod] = useState(12)
  const [decision, setDecision] = useState(null)
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setDecision(null)

    try {
      const response = await apiClient.post('public/decision', {
        personalCode,
        loanAmount: Number(loanAmount),
        loanPeriod: Number(loanPeriod)
      })
      setDecision(response.data)
    } catch (err) {
      console.error(err)
      setError('Failed to fetch decision. Please try again.')
    }
  }

  return (
    <main className="app">
      <h1>Loan Decision</h1>

      <form className="form" onSubmit={handleSubmit}>
        <label htmlFor="personalCode">Personal Code</label>
        <input
          id="personalCode"
          type="text"
          value={personalCode}
          onChange={(e) => setPersonalCode(e.target.value)}
          required
        />

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

        <button type="submit">Get Decision</button>
      </form>

      {error && <p className="error">{error}</p>}

      {decision && (
        <section className="result">
          <h2>{decision.approved ? 'Approved' : 'Denied'}</h2>
          <p>Amount: {decision.approvedAmount} EUR</p>
          <p>Period: {decision.approvedPeriod} months</p>
          {decision.message && <p>{decision.message}</p>}
        </section>
      )}
    </main>
  )
}

export default App
