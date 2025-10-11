import React, { useEffect, useMemo, useState } from 'react'
import '../styles/Admin.css'
import { endpoints, apiGet, apiJson } from '../services/api'
import { useNavigate } from 'react-router-dom'

// Enhanced Section Component with better UX
function Section({ title, items, columns, onCreate, onUpdate, onDelete, empty, loading, error }) {
  const [draft, setDraft] = useState({})
  const [editingItem, setEditingItem] = useState(null)
  const [showCreateForm, setShowCreateForm] = useState(false)
  const [operationLoading, setOperationLoading] = useState(false)

  const handleCreate = async () => {
    if (!Object.values(draft).some(v => v)) {
      alert('Vui lòng nhập ít nhất một trường')
      return
    }
    
    setOperationLoading(true)
    try {
      await onCreate(draft)
      setDraft({})
      setShowCreateForm(false)
    } catch (error) {
      alert('Lỗi khi tạo: ' + error.message)
    } finally {
      setOperationLoading(false)
    }
  }

  const handleUpdate = async (item) => {
    setOperationLoading(true)
    try {
      await onUpdate(item)
      setEditingItem(null)
    } catch (error) {
      alert('Lỗi khi cập nhật: ' + error.message)
    } finally {
      setOperationLoading(false)
    }
  }

  const handleDelete = async (id) => {
    if (!confirm('Bạn có chắc chắn muốn xóa?')) return
    
    setOperationLoading(true)
    try {
      await onDelete(id)
    } catch (error) {
      alert('Lỗi khi xóa: ' + error.message)
    } finally {
      setOperationLoading(false)
    }
  }

  return (
    <div className="section-card">
      <div className="admin-header">
        <h2 className="section-title">{title}</h2>
        <div className="header-actions">
          <button 
            onClick={() => setShowCreateForm(!showCreateForm)} 
            className="btn primary"
            disabled={operationLoading}
          >
            {showCreateForm ? 'Hủy' : 'Thêm mới'}
          </button>
        </div>
      </div>

      {error && (
        <div className="error-message">
          Lỗi: {error.message}
        </div>
      )}

      <div className="table-wrap">
        <table className="admin-table">
          <thead>
            <tr>
              {columns.map(c => (
                <th key={c.key}>{c.label}</th>
              ))}
              <th style={{ width: 160 }}>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={columns.length + 1} className="center">
                  <div className="loading-spinner">Đang tải...</div>
                </td>
              </tr>
            ) : (items || []).length === 0 ? (
              <tr>
                <td colSpan={columns.length + 1} className="empty-row center">
                  {empty}
                </td>
              </tr>
            ) : (
              items.map(item => (
                <tr key={item.id || item.account_id}>
                  {columns.map(col => (
                    <td key={col.key}>
                      {editingItem?.id === item.id || editingItem?.account_id === item.account_id ? (
                        <input 
                          className="admin-input"
                          defaultValue={item[col.key] || ''}
                          onChange={e => item[col.key] = e.target.value}
                        />
                      ) : (
                        <span className="cell-content">
                          {col.type === 'password' ? '••••••••' : (item[col.key] || '-')}
                        </span>
                      )}
                    </td>
                  ))}
                  <td className="actions">
                    {editingItem?.id === item.id || editingItem?.account_id === item.account_id ? (
                      <>
                        <button 
                          onClick={() => handleUpdate(item)} 
                          className="btn success"
                          disabled={operationLoading}
                        >
                          Lưu
                        </button>
                        <button 
                          onClick={() => setEditingItem(null)} 
                          className="btn ghost"
                          disabled={operationLoading}
                        >
                          Hủy
                        </button>
                      </>
                    ) : (
                      <>
                        <button 
                          onClick={() => setEditingItem(item)} 
                          className="btn primary"
                          disabled={operationLoading}
                        >
                          Sửa
                        </button>
                        <button 
                          onClick={() => handleDelete(item.id || item.account_id)} 
                          className="btn danger"
                          disabled={operationLoading}
                        >
                          Xóa
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showCreateForm && (
        <div className="create-area">
          <div className="create-title">Tạo mới {title.toLowerCase()}</div>
          <div className="create-grid">
            {columns.map(c => (
              <div key={c.key} className="field">
                <input 
                  placeholder={c.label}
                  className="admin-input"
                  type={c.type || 'text'}
                  value={draft[c.key] || ''}
                  onChange={e => setDraft(d => ({ ...d, [c.key]: e.target.value }))}
                />
              </div>
            ))}
          </div>
          <div className="create-actions">
            <button 
              onClick={handleCreate} 
              className="btn success"
              disabled={operationLoading}
            >
              {operationLoading ? 'Đang tạo...' : 'Tạo'}
            </button>
            <button 
              onClick={() => {
                setShowCreateForm(false)
                setDraft({})
              }} 
              className="btn ghost"
              disabled={operationLoading}
            >
              Hủy
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

export default function Admin() {
  const nav = useNavigate()
  const session = useMemo(() => {
    try { 
      return JSON.parse(localStorage.getItem('session') || 'null') 
    } catch { 
      return null 
    }
  }, [])
  const role = session?.role

  // State management
  const [accounts, setAccounts] = useState([])
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // Authentication check
  useEffect(() => {
    if (role !== 'admin' && role !== 'staff') {
      nav('/login')
    }
  }, [role, nav])

  // Data loading
  const reload = async () => {
    setLoading(true)
    setError(null)
    try {
      const [accountsData, employeesData] = await Promise.all([
        apiGet(endpoints.accounts()),
        apiGet(endpoints.employees())
      ])
      setAccounts(accountsData || [])
      setEmployees(employeesData || [])
    } catch (err) {
      setError(err)
      console.error('Failed to load data:', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (role === 'admin' || role === 'staff') {
      reload()
    }
  }, [role])

  // Enhanced CRUD operations with better error handling
  const createAccount = async (data) => {
    await apiJson(endpoints.accounts(), 'POST', data)
    await reload()
  }

  const updateAccount = async (data) => {
    const id = data.id || data.account_id
    await apiJson(`${endpoints.accounts()}/${id}`, 'PUT', data)
    await reload()
  }

  const deleteAccount = async (id) => {
    await apiJson(`${endpoints.accounts()}/${id}`, 'DELETE')
    await reload()
  }

  const createEmployee = async (data) => {
    await apiJson(endpoints.employees(), 'POST', data)
    await reload()
  }

  const updateEmployee = async (data) => {
    const id = data.id || data.employee_id
    await apiJson(`${endpoints.employees()}/${id}`, 'PUT', data)
    await reload()
  }

  const deleteEmployee = async (id) => {
    await apiJson(`${endpoints.employees()}/${id}`, 'DELETE')
    await reload()
  }

  const handleLogout = () => {
    localStorage.removeItem('session')
    nav('/login')
  }

  if (loading && (accounts.length === 0 && employees.length === 0)) {
    return (
      <div className="admin-page">
        <div className="container-admin">
          <div className="loading-container">
            <div className="loading-spinner">Đang tải dữ liệu...</div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="admin-page">
      <div className="container-admin">
        <div className="admin-header">
          <h1 className="admin-title">Bảng điều khiển quản trị</h1>
          <div className="header-actions">
            <div className="user-info">
              <span className="user-name">Xin chào, {session?.userName || 'Admin'}</span>
              <span className="user-role">({role === 'admin' ? 'Quản trị viên' : 'Nhân viên'})</span>
            </div>
            <button onClick={handleLogout} className="btn ghost">
              Đăng xuất
            </button>
          </div>
        </div>

        {error && (
          <div className="error-banner">
            <p>Lỗi tải dữ liệu: {error.message}</p>
            <button onClick={reload} className="btn primary">
              Thử lại
            </button>
          </div>
        )}

        <Section
          title="Tài khoản khách (Accounts)"
          items={accounts}
          columns={[
            { key: 'userName', label: 'Tên người dùng' },
            { key: 'email', label: 'Email' },
            { key: 'password', label: 'Mật khẩu', type: 'password' },
            { key: 'phoneNumber', label: 'Số điện thoại' },
            { key: 'is_active', label: 'Trạng thái' }
          ]}
          onCreate={createAccount}
          onUpdate={updateAccount}
          onDelete={deleteAccount}
          empty="Chưa có tài khoản nào"
          loading={loading}
          error={error}
        />

        <Section
          title="Nhân viên (Employees)"
          items={employees}
          columns={[
            { key: 'employeeCode', label: 'Mã nhân viên' },
            { key: 'position', label: 'Chức vụ' },
            { key: 'department', label: 'Phòng ban' },
            { key: 'salary', label: 'Lương' },
            { key: 'status', label: 'Trạng thái' }
          ]}
          onCreate={createEmployee}
          onUpdate={updateEmployee}
          onDelete={deleteEmployee}
          empty="Chưa có nhân viên nào"
          loading={loading}
          error={error}
        />
      </div>
    </div>
  )
}


