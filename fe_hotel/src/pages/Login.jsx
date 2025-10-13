import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { endpoints, apiGet, apiJson } from '../services/api'
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google'
import '../styles/login.css'

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID

export default function Login(){
  const nav = useNavigate()
  const [role, setRole] = useState('user') // user | staff | admin
  const [email, setEmail] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  async function handleGoogleLogin(response) {
    try {
      // Send the Google token to your backend for verification
      const userData = await apiJson(endpoints.googleLogin(), 'POST', { 
        token: response.credential 
      })
      
      localStorage.setItem('session', JSON.stringify({ 
        role: userData.role, 
        id: userData.id, 
        name: userData.name,
        loginType: userData.loginType,
        token: userData.token
      }))
      nav('/')
    } catch (err) {
      setError('Google login failed: ' + err.message)
    }
  }

  async function handleSubmit(e){
    e.preventDefault()
    setError('')
    try {
      // Validate required fields - ALL roles now require password
      if(role === 'user'){
        if(!email || !password) return setError('Vui lòng nhập email và mật khẩu hoặc đăng nhập bằng Google')
      } else {
        if(!(username || email) || !password) {
          return setError('Vui lòng nhập email/username và mật khẩu')
        }
      }

      // Call backend authentication
      const loginRequest = {
        email: email,
        username: username,
        password: password,
        role: role
      }

      const response = await apiJson(endpoints.login(), 'POST', loginRequest)
      
      localStorage.setItem('session', JSON.stringify({ 
        role: response.role, 
        id: response.id, 
        name: response.name,
        loginType: response.loginType,
        token: response.token
      }))
      
      // Navigate based on role
      if(response.role === 'user') {
        nav('/')
      } else {
        nav('/admin')
      }
      
    } catch(err){
      setError('Đăng nhập thất bại: ' + err.message)
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

        <div>
          <label className="block mb-1">
            {role === 'user' ? 'Email' : 'Gmail hoặc Username'}
          </label>
          <input 
            value={role === 'user' ? email : (email||username)} 
            onChange={e=>{
              if(role === 'user') {
                setEmail(e.target.value)
              } else {
                setEmail(e.target.value); 
                setUsername(e.target.value)
              }
            }} 
            className="border px-3 py-2 w-full" 
            placeholder={role === 'user' ? 'you@example.com' : 'admin@gmail.com hoặc admin'} 
          />
        </div>
        <div>
          <label className="block mb-1">Mật khẩu</label>
          <input type="password" value={password} onChange={e=>setPassword(e.target.value)} className="border px-3 py-2 w-full" placeholder="Nhập mật khẩu" />
        </div>

        {error && <div className="text-red-600 text-sm">{error}</div>}

        <button className="bg-black text-white px-4 py-2">Đăng nhập</button>
      </form>

      {role === 'user' && (
        <div className="mt-6">
          <div className="text-center text-gray-500 mb-4">Hoặc đăng nhập bằng</div>
          <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID}>
            <GoogleLogin
              onSuccess={handleGoogleLogin}
              onError={() => setError('Google login failed')}
              theme="outline"
              size="large"
              width="100%"
              text="signin_with"
              shape="rectangular"
            />
          </GoogleOAuthProvider>
        </div>
      )}
    </div>
  )
}


