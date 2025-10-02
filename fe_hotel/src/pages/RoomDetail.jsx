import React, { useEffect, useState, useMemo } from 'react'
import { useParams } from 'react-router-dom'
import axios from 'axios'
import { Container, Row, Col, Badge, Card, Form, Button } from 'react-bootstrap'
import '../styles/room-detail.css'

export default function RoomDetail() {
  const { id } = useParams()

  // Dùng env nếu có, fallback /api, và bỏ dấu / cuối để tránh //rooms
  const API = (import.meta.env.VITE_API_BASE ?? '/api').replace(/\/$/, '')

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [data, setData] = useState(null)

  useEffect(() => {
    window.scrollTo(0, 0)
    setLoading(true)

    const url = `${API}/rooms/${id}`
    axios.get(url, { headers: { Accept: 'application/json' } })
      .then(r => {
        // Log để nhìn rõ đang nhận gì
        console.log('DETAIL response URL:', r.request?.responseURL || url)
        console.log('DETAIL content-type:', r.headers?.['content-type'])
        console.log('DETAIL typeof data:', typeof r.data)

        // Nếu proxy trả về HTML (string, bắt đầu bằng <!doctype) => đang không qua API
        if (typeof r.data === 'string' && r.data.trim().startsWith('<!')) {
          throw new Error('Proxy returned HTML instead of JSON')
        }
        setData(r.data)
      })
      .catch(async e => {
        // Thử fallback gọi thẳng BE khi proxy lỗi (chỉ trong DEV)
        console.warn('DETAIL primary request failed -> try BE direct:', e?.message)
        try {
          const direct = await axios.get(`http://localhost:8080/api/rooms/${id}`)
          setData(direct.data)
        } catch (ee) {
          console.error('DETAIL direct request error:', ee)
          setError(ee.message)
        }
      })
      .finally(() => setLoading(false))
  }, [API, id])

  const room = data?.room
  const price = room?.priceVnd ?? 0
  const discount = room?.discount ?? 0
  const priceStr = useMemo(() => price.toLocaleString('vi-VN') + '₫', [price])

  if (loading) return <Container className="py-5"><div className="alert alert-info">Đang tải chi tiết phòng…</div></Container>
  if (error)   return <Container className="py-5"><div className="alert alert-danger">Lỗi: {error}</div></Container>
  if (!room)   return <Container className="py-5"><div className="alert alert-warning">Không tìm thấy phòng.</div></Container>

  return (
    <Container className="detail-wrap py-4">
      {/* Header */}
      <h2 className="fw-bold mb-2">{room.name}</h2>
      <div className="d-flex align-items-center gap-3 text-muted mb-3">
        <div>⭐ {room.rating ?? 4.7} <span className="small">({room.reviews ?? 0} đánh giá)</span></div>
        <div>👥 {room.capacity} khách</div>
        <div>🛏️ 1 giường đôi</div>
        <div>📐 {room.sizeSqm}m²</div>
        {data?.floorRange && <div>📍 {data.floorRange}</div>}
      </div>

      {/* Ảnh lớn + booking box */}
      <Row className="g-4">
        <Col lg={8}>
          <div className="main-photo position-relative">
            <img src={data?.gallery?.[0] ?? room.imageUrl} alt={room.name} />
            <span className="photo-count">{(data?.gallery?.length || 1)} / {(data?.gallery?.length || 1)}</span>
          </div>
          <div className="thumbs mt-2">
            {(data?.gallery ?? [room.imageUrl]).map((src, i) => (
              <img key={i} src={src} alt={`thumb-${i}`} />
            ))}
          </div>

          {/* Mô tả & điểm nổi bật */}
          <Card className="card-soft mt-4">
            <Card.Body>
              <Card.Title className="h5 mb-3">Mô tả phòng</Card.Title>
              <p className="mb-3">{data?.description}</p>
              <div className="mb-2 fw-semibold">Đặc điểm nổi bật:</div>
              <div className="d-flex flex-wrap gap-2">
                {(data?.highlights ?? room.amenities ?? []).map((x,i)=>(
                  <Badge key={i} bg="light" text="dark" className="badge-light">{x}</Badge>
                ))}
              </div>
            </Card.Body>
          </Card>

          {/* Tiện nghi theo nhóm */}
          <Card className="card-soft mt-4">
            <Card.Body>
              <Card.Title className="h5 mb-3">Tiện nghi phòng</Card.Title>
              <Row className="gy-3">
                {Object.entries(data?.amenities ?? {}).map(([group, items]) => (
                  <Col md={6} key={group}>
                    <div className="fw-semibold mb-2">{group}</div>
                    <ul className="list-unstyled m-0">
                      {items.map((it,i)=><li key={i} className="text-muted">• {it}</li>)}
                    </ul>
                  </Col>
                ))}
              </Row>
            </Card.Body>
          </Card>

          {/* Đánh giá ngắn gọn */}
          <Card className="card-soft mt-4">
            <Card.Body>
              <Card.Title className="h5 mb-3">Đánh giá gần đây</Card.Title>
              {(data?.reviews ?? []).map((rv, i)=>(
                <div key={i} className="d-flex gap-3 align-items-start mb-3">
                  <img src={rv.avatar} alt={rv.name} className="avatar"/>
                  <div>
                    <div className="fw-semibold">{rv.author} <span className="text-warning">⭐ {rv.stars}</span></div>
                    <div className="small text-muted mb-1">{rv.timeAgo}</div>
                    <div>{rv.content}</div>
                  </div>
                </div>
              ))}
            </Card.Body>
          </Card>
        </Col>

        {/* Booking box */}
        <Col lg={4}>
          <Card className="card-soft sticky">
            <Card.Body>
              {discount>0 && (
                <div className="text-decoration-line-through text-muted small">
                  {(Math.round(price*1.2)).toLocaleString('vi-VN')}₫
                </div>
              )}
              <div className="h3 mb-1 text-danger">{priceStr}</div>
              <div className="small text-muted mb-3">/ đêm</div>

              <Form>
                <Row className="g-2">
                  <Col md={6}>
                    <Form.Label className="small">Nhận phòng</Form.Label>
                    <Form.Control type="date"/>
                  </Col>
                  <Col md={6}>
                    <Form.Label className="small">Trả phòng</Form.Label>
                    <Form.Control type="date"/>
                  </Col>
                </Row>
                <Form.Group className="mt-2">
                  <Form.Label className="small">Số khách</Form.Label>
                  <Form.Control type="number" min={1} defaultValue={2}/>
                </Form.Group>
                <Button className="w-100 mt-3" variant="danger">Đặt phòng ngay</Button>
                <ul className="mt-3 small text-muted list-unstyled">
                  <li>✔ Miễn phí hủy trong 24 giờ</li>
                  <li>✔ Thanh toán khi nhận phòng</li>
                  <li>✔ Xác nhận đặt phòng ngay lập tức</li>
                </ul>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  )
}
