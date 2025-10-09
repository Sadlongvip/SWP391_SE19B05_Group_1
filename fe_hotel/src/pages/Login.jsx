import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { endpoints, apiGet } from '../services/api'

export default function Login(){
  const nav = useNavigate()
  const [role, setRole] = useState('user') // user | staff | admin
  const [email, setEmail] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  async function handleSubmit(e){
    e.preventDefault()
    setError('')
    try {
      if(role === 'user'){
        if(!email) return setError('Vui lòng nhập email')
        // Demo: find account by email from list
        const accounts = await apiGet(endpoints.accounts())
        const found = accounts.find(a => (a.email||'').toLowerCase() === email.toLowerCase())
        if(found){
          localStorage.setItem('session', JSON.stringify({ role: 'user', id: found.id, name: found.userName }))
          return nav('/')
        }
        return setError('Không tìm thấy tài khoản với email này')
      } else {
        if(!(username || email) || !password) return setError('Nhập gmail/username và mật khẩu')
        // Demo: staff/admin validation: username/email + password must match existing staff by email or staffId
        if(role === 'staff'){
          const staffList = await apiGet(endpoints.staffs())
          const found = staffList.find(s => (s.email === email && password) || (s.staffId === username && password))
          if(found){
            localStorage.setItem('session', JSON.stringify({ role: 'staff', id: found.id, name: found.staffName }))
            return nav('/admin')
          }
          return setError('Thông tin đăng nhập nhân viên không đúng')
        } else {
          // Temporary admin: hardcoded demo account
          const ok = (username === 'admin' || email === 'admin@gmail.com') && password === 'admin123'
          if(ok){
            localStorage.setItem('session', JSON.stringify({ role: 'admin', id: 0, name: 'Administrator' }))
            return nav('/admin')
          }
          return setError('Thông tin đăng nhập admin không đúng')
        }
      }
    } catch(err){
      setError('Đăng nhập thất bại')
    }
  }

  return (
    <div className="container mx-auto px-4 py-10" style={{maxWidth: 560}}>
      <h1 className="text-2xl font-semibold mb-6">Đăng nhập</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block mb-1">Vai trò</label>
          <select value={role} onChange={e=>setRole(e.target.value)} className="border px-3 py-2 w-full">
            <option value="user">Khách (Account)</option>
            <option value="staff">Nhân viên (Staff)</option>
            <option value="admin">Quản trị (Admin)</option>
          </select>
        </div>

        {role === 'user' ? (
          <div>
            <label className="block mb-1">Email</label>
            <input value={email} onChange={e=>setEmail(e.target.value)} className="border px-3 py-2 w-full" placeholder="you@example.com" />
          </div>
        ) : (
          <>
            <div>
              <label className="block mb-1">Gmail hoặc Username</label>
              <input value={email||username} onChange={e=>{ setEmail(e.target.value); setUsername(e.target.value) }} className="border px-3 py-2 w-full" placeholder="admin@gmail.com hoặc admin" />
            </div>
            <div>
              <label className="block mb-1">Mật khẩu</label>
              <input type="password" value={password} onChange={e=>setPassword(e.target.value)} className="border px-3 py-2 w-full" />
            </div>
          </>
        )}

        {error && <div className="text-red-600 text-sm">{error}</div>}

        <button className="bg-black text-white px-4 py-2">Đăng nhập</button>
      </form>
    </div>
  )
}


