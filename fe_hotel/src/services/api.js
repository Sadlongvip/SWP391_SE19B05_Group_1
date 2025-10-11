const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api'

export const endpoints = {
  rooms: () => `${API_BASE}/rooms`,
  accounts: () => `${API_BASE}/accounts`,
  employees: () => `${API_BASE}/employees`
}

export async function apiGet(url){
  const res = await fetch(url, { credentials: 'include' })
  if(!res.ok) throw new Error(`GET ${url} -> ${res.status}`)
  return res.json()
}

export async function apiJson(url, method, body){
  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
    credentials: 'include'
  })
  if(!res.ok) throw new Error(`${method} ${url} -> ${res.status}`)
  return res.json?.() ?? null
}

export default API_BASE
