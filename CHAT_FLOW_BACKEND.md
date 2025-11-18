# Luồng hoạt động của hệ thống chat (Backend)

## 1. Đăng nhập (Authentication)
- Người dùng gửi thông tin đăng nhập (email, password, captcha) qua API `/api/auth/login`.
- Backend xác thực thông tin, trả về JWT token nếu thành công.

## 2. Khởi tạo chat (Init Chat)
- Frontend gọi API `/api/chat/init` để lấy thông tin user, roomId, v.v.
- Backend trả về thông tin user, roomId, token,...

## 3. Kết nối WebSocket
- Frontend kết nối tới endpoint WebSocket `/ws-chat` sử dụng SockJS + STOMP.
- Token JWT được gửi lên qua header `Authorization` khi handshake.
- Backend xác thực token qua `WebSocketAuthInterceptor`.

## 4. Đăng ký nhận tin nhắn (Subscribe)
- Sau khi kết nối thành công, frontend subscribe vào các topic:
    - `/user/queue/private` (tin nhắn riêng)
    - `/topic/public` (nếu có chat public)

## 5. Gửi tin nhắn
- Khi người dùng gửi tin nhắn, frontend gửi message qua STOMP tới `/app/chat.sendPrivate`.
- Payload dạng: `receiverId_messageContent` (ví dụ: `5_Hello admin`)
- Backend nhận, parse, xác định roomId, lưu message vào DB, gửi lại cho các client liên quan qua topic tương ứng.

## 6. Nhận tin nhắn
- Backend gửi tin nhắn realtime về cho các client đã subscribe topic tương ứng.
- Frontend nhận và hiển thị tin nhắn.

## 7. Danh sách cuộc trò chuyện
- Frontend gọi API `/api/messages/conversations` để lấy danh sách các cuộc trò chuyện (theo user, admin).
- Backend trả về danh sách roomId, lastMessage, user info,...

---

# Mô tả cách frontend kết nối với backend chat

## 1. Đăng nhập
- Gửi POST `/api/auth/login` với email, password, captcha_response.
- Lưu access_token trả về để dùng cho các request tiếp theo.

## 2. Khởi tạo chat
- Gửi POST `/api/chat/init` với access_token trong header.
- Nhận về thông tin user, roomId, v.v.

## 3. Kết nối WebSocket
- Sử dụng SockJS + STOMP để connect tới `ws://localhost:8080/ws-chat`.
- Gửi access_token vào header `Authorization` khi connect:
  ```js
  const socket = new SockJS('http://localhost:8080/ws-chat');
  const stompClient = Stomp.over(socket);
  stompClient.connect({ Authorization: 'Bearer ' + accessToken }, ...);
  ```

## 4. Subscribe topic
- Sau khi connect thành công, subscribe vào `/user/queue/private` để nhận tin nhắn riêng.
  ```js
  stompClient.subscribe('/user/queue/private', onMessageReceived);
  ```

## 5. Gửi tin nhắn
- Gửi message qua STOMP tới `/app/chat.sendPrivate`:
  ```js
  stompClient.send('/app/chat.sendPrivate', {}, `${receiverId}_${message}`);
  ```

## 6. Nhận tin nhắn
- Xử lý callback khi nhận tin nhắn từ topic đã subscribe.

## 7. Lấy danh sách cuộc trò chuyện
- Gọi API `/api/messages/conversations` với access_token để lấy danh sách các cuộc trò chuyện và last message.

---

## Tóm tắt:
- Đăng nhập lấy token
- Init chat lấy info
- Kết nối WebSocket với token
- Subscribe topic
- Gửi/nhận tin nhắn qua STOMP
- Lấy danh sách cuộc trò chuyện qua REST API
