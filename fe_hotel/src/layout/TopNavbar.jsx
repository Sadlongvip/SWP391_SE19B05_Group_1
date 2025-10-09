import React from 'react'
import Container from 'react-bootstrap/Container'
import Nav from 'react-bootstrap/Nav'
import Navbar from 'react-bootstrap/Navbar'
import Button from 'react-bootstrap/Button'
import { Link } from 'react-router-dom'

export default function TopNavbar(){
  return (
    <Navbar expand="lg" className="bg-body-tertiary py-3 sticky-top shadow-sm">
      <Container>
        <Navbar.Brand as={Link} to="/">LuxeStay</Navbar.Brand>
        <Navbar.Toggle aria-controls="main-nav" />
        <Navbar.Collapse id="main-nav">
          <Nav className="me-auto">
            {/* <Nav.Link as={Link} to="/">Trang chủ</Nav.Link> */}
            <Nav.Link href="#rooms">Phòng</Nav.Link>
            <Nav.Link href="#amenities">Tiện nghi</Nav.Link>
            <Nav.Link href="#about">Giới thiệu</Nav.Link>
            <Nav.Link href="#contact">Liên hệ</Nav.Link>
          </Nav>
          <div className="d-flex gap-2">
            <Button as={Link} to="/login" variant="outline-secondary">Đăng nhập</Button>
            <Button as={Link} to="/admin" variant="outline-primary">Quản trị</Button>
            <Button variant="danger">Đặt phòng</Button>
          </div>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  )
}
