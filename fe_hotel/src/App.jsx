import React from 'react'
import { Routes, Route } from 'react-router-dom'
import TopNavbar from './layout/TopNavbar'
import Footer from './layout/Footer'
import Home from './pages/Home'
import Search from './pages/Search'
import RoomDetail from './pages/RoomDetail'

export default function App(){
  return (
    <div>
      <TopNavbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/search" element={<Search />} />
        <Route path="/rooms/:id" element={<RoomDetail />} /> 
      </Routes>
      <Footer />
    </div>
  )
}
