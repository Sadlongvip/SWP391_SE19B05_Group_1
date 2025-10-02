package com.luxestay.hotel.service;

import com.luxestay.hotel.dto.PagedResponse;
import com.luxestay.hotel.dto.RoomDetail;
import com.luxestay.hotel.dto.RoomSearchCriteria;
import com.luxestay.hotel.model.Review;
import com.luxestay.hotel.model.Room;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {

    /* ---------- Helpers ---------- */

    private String img(String seed, int w, int h){ return "https://picsum.photos/seed/"+seed+"/"+w+"/"+h; }
    private String img(String seed){ return img(seed, 1200, 800); }

    private Room r(long id, String name, String type, int capacity, int size, int price,
                   String[] amenities, String seed, boolean popular,
                   double rating, int reviews, Integer discount) {
        Room rm = new Room(id, name, type, capacity, size, price, amenities, img(seed, 800, 450), popular);
        rm.setRating(rating); rm.setReviews(reviews); rm.setDiscount(discount);
        return rm;
    }

    /* ---------- DATA GIẢ: 12 PHÒNG ---------- */
    private final List<Room> ROOMS = List.of(
            r(1,  "Phòng Deluxe Tầm Nhìn Vườn",        "Deluxe",       2, 32,  2_200_000,
                    new String[]{"Tầm nhìn vườn","Ban công","Minibar"}, "deluxe_garden", true, 4.7, 156, null),
            r(2,  "Phòng Deluxe Tầm Nhìn Thành Phố",   "Deluxe",       2, 35,  2_500_000,
                    new String[]{"Tầm nhìn thành phố","Ban công riêng","Minibar"}, "deluxe_city", true, 4.8, 124, 17),
            r(3,  "Phòng Family Suite",                 "Family",       5, 55,  3_800_000,
                    new String[]{"Phòng khách","Khu vực trà & cà phê","Tầm nhìn thành phố"}, "family_suite", true, 4.6, 78, null),
            r(4,  "Phòng Suite Tầm Nhìn Biển",          "Suite",        4, 65,  4_200_000,
                    new String[]{"Phòng khách riêng","Tầm nhìn biển","Bồn tắm jacuzzi"}, "suite_ocean", true, 4.9, 89, 17),
            r(5,  "Phòng Presidential Suite",           "Presidential", 6, 120, 8_500_000,
                    new String[]{"2 phòng ngủ","Phòng ăn riêng","Butler service"}, "presidential", true, 5.0, 45, null),
            r(6,  "Phòng Junior Suite",                 "Suite",        3, 48,  3_200_000,
                    new String[]{"Phòng khách","Ban công","Minibar"}, "junior_suite", false, 4.5, 67, null),
            r(7,  "Phòng Executive Suite",              "Suite",        4, 72,  5_000_000,
                    new String[]{"Phòng khách riêng","Bàn làm việc","Room-Service"}, "exec_suite", false, 4.7, 54, 10),
            r(8,  "Phòng Deluxe Twin Thành Phố",        "Deluxe",       2, 34,  2_400_000,
                    new String[]{"Tầm nhìn thành phố","Ban công","WiFi miễn phí"}, "deluxe_twin", false, 4.6, 90, null),
            r(9,  "Phòng Superior",                     "Superior",     2, 28,  1_800_000,
                    new String[]{"WiFi miễn phí","Bàn làm việc","Minibar"}, "superior", false, 4.3, 38, null),
            r(10, "Penthouse Presidential",             "Presidential", 6, 180, 12_000_000,
                    new String[]{"2 phòng ngủ","Butler service","Tầm nhìn biển"}, "penthouse", false, 5.0, 21, null),
            r(11, "Studio King",                        "Studio",       2, 30,  2_000_000,
                    new String[]{"Bàn làm việc","Minibar","WiFi miễn phí"}, "studio_king", false, 4.4, 31, null),
            r(12, "Family Connecting",                  "Family",       6, 70,  4_000_000,
                    new String[]{"2 phòng ngủ","Khu vực trà & cà phê","Phòng khách"}, "family_connect", false, 4.6, 42, null)
    );

    /* ---------- LIST & SEARCH ---------- */

    public List<Room> listRooms() { return ROOMS; }

    public PagedResponse<Room> search(RoomSearchCriteria c){
        List<Room> data = new ArrayList<>(ROOMS);

        // lọc types
        if (c.getTypes()!=null && !c.getTypes().isEmpty()){
            Set<String> types = c.getTypes().stream()
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());
            data = data.stream()
                    .filter(r -> types.contains(r.getType().toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList()); // <== thay toList()
        }

        // lọc amenities (ít nhất 1 trùng)
        if (c.getAmenities()!=null && !c.getAmenities().isEmpty()){
            Set<String> need = c.getAmenities().stream()
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());
            data = data.stream().filter(r -> {
                String[] am = r.getAmenities()==null ? new String[0] : r.getAmenities();
                for (String h : am){
                    if (need.contains(h.toLowerCase(Locale.ROOT))) return true;
                }
                return false;
            }).collect(Collectors.toList()); // <== thay toList()
        }

        // giá & khách
        if (c.getPriceMax()!=null)
            data = data.stream()
                    .filter(r -> r.getPriceVnd() <= c.getPriceMax())
                    .collect(Collectors.toList()); // <== thay toList()

        if (c.getGuests()!=null)
            data = data.stream()
                    .filter(r -> r.getCapacity() >= c.getGuests())
                    .collect(Collectors.toList()); // <== thay toList()

        // sort – an toàn kiểu và null
        Comparator<Room> cmp = Comparator.comparingInt(Room::getPriceVnd);
        if ("priceDesc".equalsIgnoreCase(c.getSort())) {
            cmp = Comparator.comparingInt(Room::getPriceVnd).reversed();
        } else if ("ratingDesc".equalsIgnoreCase(c.getSort())) {
            cmp = Comparator.comparing(
                    Room::getRating,
                    Comparator.nullsLast(Double::compareTo)
            ).reversed();
        }

        // (không còn immutable nên sort OK)
        data.sort(cmp);

        // paging an toàn
        int page = c.getPage()==null ? 0 : c.getPage();
        int size = c.getSize()==null ? 10 : Math.max(1, c.getSize());
        int from = Math.min(page * size, data.size());
        int to   = Math.min(from + size, data.size());

        return new PagedResponse<>(data.subList(from, to), data.size(), page, size);
    }

    /* ---------- ROOM DETAIL ---------- */

    public RoomDetail getDetail(Long id){
        Room room = ROOMS.stream().filter(r -> r.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        RoomDetail d = new RoomDetail();
        d.setRoom(room);
        d.setFloorRange("Tầng 15-20");
        d.setDescription(
                "Phòng " + room.getName() + " với tầm nhìn đẹp, trang bị tiện nghi hiện đại. " +
                        "Không gian rộng rãi, phù hợp cho cặp đôi, gia đình hoặc khách công tác."
        );
        d.setHighlights(List.of(room.getAmenities()));

        String seed = room.getImageUrl().replaceAll(".*/seed/([^/]+)/.*", "$1");
        d.setGallery(List.of(
                img(seed),
                img(seed+"-b"),
                img(seed+"-c")
        ));
        d.setAmenities(Map.of(
                "Tiện nghi cơ bản", List.of("WiFi miễn phí","Điều hòa","TV màn hình phẳng","Minibar"),
                "Dịch vụ", List.of("Dịch vụ phòng 24/7","Điện thoại","Két an toàn","Dịch vụ giặt ủi"),
                "Phòng tắm", List.of("Bồn tắm","Vòi sen","Đồ dùng tắm cao cấp","Máy sấy tóc"),
                "Tiện ích khác", List.of("Hồ bơi","Phòng gym","Spa","Bãi đỗ xe")
        ));
        d.setRatingHistogram(Map.of(5,78,4,32,3,10,2,3,1,1));
        d.setReviews(List.of(
                new Review("Nguyễn Minh Anh","https://i.pravatar.cc/64?img=1",5,
                        "Phòng rất đẹp và sạch sẽ. Nhân viên thân thiện, chuyên nghiệp.","2 tuần trước"),
                new Review("Trần Văn Hùng","https://i.pravatar.cc/64?img=12",4,
                        "Vị trí thuận tiện, phòng thoải mái.","1 tháng trước"),
                new Review("Lê Thị Mai","https://i.pravatar.cc/64?img=5",5,
                        "Dịch vụ tuyệt vời! Minibar đầy đủ, phòng tắm sạch sẽ.","1 tháng trước")
        ));
        return d;
    }
}
