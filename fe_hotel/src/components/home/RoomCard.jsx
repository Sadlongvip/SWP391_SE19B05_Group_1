import React from 'react'
import Card from 'react-bootstrap/Card'
import Button from 'react-bootstrap/Button'
import Badge from 'react-bootstrap/Badge'
import { Link } from 'react-router-dom'

const vnd = (n)=> (n||0).toLocaleString('vi-VN') + '₫'

export default function RoomCard({ room }){
  return (
    <Card className="h-100 shadow-soft rounded-2xl overflow-hidden">
      <div style={{height:200, overflow:'hidden'}}>
        <Card.Img src={room.imageUrl} alt={room.name} style={{objectFit:'cover', height:'100%', width:'100%'}}/>
      </div>
      <Card.Body>
        <div className="d-flex justify-content-between align-items-start">
          <Card.Title className="mb-1">{room.name}</Card.Title>
          {room.popular && <Badge bg="danger-subtle" text="dark">Phổ biến</Badge>}
        </div>
        <div className="text-muted small mb-2">
          👥 {room.capacity} khách • 📐 {room.sizeSqm}m²
        </div>
        <div className="fw-bold text-danger h5 mb-3">{vnd(room.priceVnd)} <span className="text-muted fw-normal small">/ đêm</span></div>
        <div className="d-flex gap-2">
          <Button as={Link} to={`/rooms/${room.id}`} variant="light" className="border">Xem chi tiết</Button>
          <Button variant="danger">Đặt ngay</Button>
        </div>
      </Card.Body>
    </Card>
  )
}
