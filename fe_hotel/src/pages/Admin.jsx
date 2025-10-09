import React, { useEffect, useMemo, useState } from 'react'
import { endpoints, apiGet, apiJson } from '../services/api'
import { useNavigate } from 'react-router-dom'

function Section({ title, items, columns, onCreate, onUpdate, onDelete, empty }){
  const [draft, setDraft] = useState({})
  return (
    <div className="mb-10">
      <div className="flex items-center justify-between mb-3">
        <h2 className="text-xl font-semibold">{title}</h2>
        <button onClick={()=>onCreate(draft)} className="bg-green-600 text-white px-3 py-1">Thêm mới</button>
      </div>
      <div className="overflow-auto border">
        <table className="min-w-full text-sm">
          <thead>
            <tr className="bg-gray-50">
              {columns.map(c=> <th key={c.key} className="text-left p-2 border-b">{c.label}</th>)}
              <th className="p-2 border-b" style={{width: 160}}>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {(items||[]).length===0 ? (
              <tr><td colSpan={columns.length+1} className="p-3 text-center text-gray-500">{empty}</td></tr>
            ) : items.map(it => (
              <tr key={it.id} className="border-b">
                {columns.map(col => (
                  <td key={col.key} className="p-2">
                    <input className="border px-2 py-1 w-full" defaultValue={it[col.key]||''}
                      onChange={e=> it[col.key]= e.target.value } />
                  </td>
                ))}
                <td className="p-2 space-x-2">
                  <button onClick={()=>onUpdate({...it})} className="bg-blue-600 text-white px-2 py-1">Lưu</button>
                  <button onClick={()=>onDelete(it.id)} className="bg-red-600 text-white px-2 py-1">Xóa</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="mt-3 p-3 border bg-gray-50">
        <div className="font-medium mb-2">Tạo mới</div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-2">
          {columns.map(c => (
            <input key={c.key} placeholder={c.label} className="border px-2 py-1" onChange={e=> setDraft(d=>({...d,[c.key]: e.target.value}))} />
          ))}
        </div>
      </div>
    </div>
  )
}

export default function Admin(){
  const nav = useNavigate()
  const session = useMemo(()=>{
    try{ return JSON.parse(localStorage.getItem('session')||'null') }catch{ return null }
  },[])
  const role = session?.role

  useEffect(()=>{
    if(role!=='admin' && role!=='staff') nav('/login')
  },[role])

  const [accounts, setAccounts] = useState([])
  const [staffs, setStaffs] = useState([])

  async function reload(){
    const [a,s] = await Promise.all([ apiGet(endpoints.accounts()), apiGet(endpoints.staffs()) ])
    setAccounts(a); setStaffs(s)
  }
  useEffect(()=>{ reload() },[])

  // Accounts CRUD
  async function createAccount(d){ await apiJson(endpoints.accounts(), 'POST', d); reload() }
  async function updateAccount(d){ await apiJson(`${endpoints.accounts()}/${d.id}`,'PUT', d); reload() }
  async function deleteAccount(id){ await fetch(`${endpoints.accounts()}/${id}`,{ method:'DELETE' }); reload() }

  // Staffs CRUD
  async function createStaff(d){ await apiJson(endpoints.staffs(), 'POST', d); reload() }
  async function updateStaff(d){ await apiJson(`${endpoints.staffs()}/${d.id}`,'PUT', d); reload() }
  async function deleteStaff(id){ await fetch(`${endpoints.staffs()}/${id}`,{ method:'DELETE' }); reload() }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-semibold">Bảng điều khiển quản trị</h1>
        <button onClick={()=>{ localStorage.removeItem('session'); nav('/login') }} className="px-3 py-1 border">Đăng xuất</button>
      </div>

      <Section
        title="Tài khoản khách (Accounts)"
        items={accounts}
        columns={[
          { key:'userName', label:'Tên người dùng' },
          { key:'email', label:'Email' },
          { key:'password', label:'Mật khẩu' },
          { key:'status', label:'Trạng thái' }
        ]}
        onCreate={createAccount}
        onUpdate={updateAccount}
        onDelete={deleteAccount}
        empty="Chưa có tài khoản"
      />

      <Section
        title="Nhân viên (Staffs)"
        items={staffs}
        columns={[
          { key:'staffId', label:'Mã nhân viên' },
          { key:'staffName', label:'Tên' },
          { key:'email', label:'Email' },
          { key:'password', label:'Mật khẩu' },
        ]}
        onCreate={createStaff}
        onUpdate={updateStaff}
        onDelete={deleteStaff}
        empty="Chưa có nhân viên"
      />
    </div>
  )
}


