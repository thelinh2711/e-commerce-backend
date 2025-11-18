# Hướng dẫn kết nối frontend chat với backend

## 1. Đăng nhập lấy token
- Gửi POST `/api/auth/login` với email, password, captcha_response.

Ví dụ request:
```json
POST /api/auth/login
{
  "email": "admin@shop.com",
  "password": "admin",
  "captcha_response": "linhconga"
}
```

Ví dụ response:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "tokens": {
      "access_token": "<JWT_TOKEN>",
      "refresh_token": "..."
    },
    "user": {
      "id": 1,
      "email": "admin@shop.com",
      "fullName": "Admin"
    }
  }
}
```

## 2. Khởi tạo chat
- Gửi POST `/api/chat/init` với access_token trong header.

Ví dụ request:
```http
POST /api/chat/init
Authorization: Bearer <access_token>
```

Ví dụ response:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "user": {
      "id": 1,
      "email": "admin@shop.com",
      "fullName": "Admin"
    },
    "roomId": "1"
  }
}
```

## 3. Kết nối WebSocket
- Sử dụng SockJS + STOMP để connect tới `ws://localhost:8080/ws-chat`.
- Gửi access_token vào header `Authorization` khi connect:
  ```js
  const socket = new SockJS('http://localhost:8080/ws-chat');
  const stompClient = Stomp.over(socket);
  stompClient.connect({ Authorization: 'Bearer ' + accessToken }, ...);
  ```

## 4. Subscribe topic
  ```js
  stompClient.subscribe('/user/queue/private', onMessageReceived);
  ```

## 5. Gửi tin nhắn
- Gửi message qua STOMP tới `/app/chat.sendPrivate`:

Ví dụ payload gửi:
```
"2_Hello admin"
```

Code mẫu:
```js
stompClient.send('/app/chat.sendPrivate', {}, `${receiverId}_${message}`);
```

## 6. Nhận tin nhắn
- Xử lý callback khi nhận tin nhắn từ topic đã subscribe.

## 7. Lấy danh sách cuộc trò chuyện
- Gọi API `/api/messages/conversations` với access_token để lấy danh sách các cuộc trò chuyện và last message.
---

## 3. Lấy lịch sử tin nhắn của một đoạn chat (room)
- Gửi GET `/api/messages/room/{roomId}` với access_token để lấy toàn bộ tin nhắn của một phòng chat.

Ví dụ request:
```http
GET /api/messages/room/2
Authorization: Bearer <access_token>
```

Ví dụ response:
```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": 1,
      "content": "Hello admin",
      "createdAt": "2025-11-18T10:00:00Z",
      "senderUsername": "user2@shop.com",
      "receiverUsername": "admin@shop.com",
      "roomId": "2"
    },
    {
      "id": 2,
      "content": "Chào bạn!",
      "createdAt": "2025-11-18T10:01:00Z",
      "senderUsername": "admin@shop.com",
      "receiverUsername": "user2@shop.com",
      "roomId": "2"
    }
  ]
}
```

---

GET /api/messages/conversations
Authorization: Bearer <access_token>
```

Ví dụ response:
```json
---

## 4. Khởi tạo đoạn chat mới (nếu chưa có)
- Nếu là admin, có thể chủ động khởi tạo đoạn chat với user bằng cách gửi tin nhắn đầu tiên qua socket (không cần REST API riêng).
- Nếu là user, chỉ cần gửi tin nhắn tới admin, backend sẽ tự động tạo room nếu chưa có.

---
{
  "message": "Success",
  "result": [
    {
      "roomId": "2",
      "lastMessage": {
        "content": "Hello admin",
        "createdAt": "2025-11-18T10:00:00Z",
        "senderUsername": "user2@shop.com"
      },
      "user": {
---

## 6. Nhận tin nhắn realtime qua socket
- Khi có tin nhắn mới, backend sẽ gửi về topic mà client đã subscribe (`/user/queue/private`).
- Hàm callback sẽ nhận được payload dạng JSON:

Ví dụ:
```json
{
  "id": 3,
  "content": "Ok bạn!",
  "createdAt": "2025-11-18T10:02:00Z",
  "senderUsername": "admin@shop.com",
  "receiverUsername": "user2@shop.com",
  "roomId": "2"
}
```

Code mẫu xử lý:
```js
stompClient.subscribe('/user/queue/private', function(payload) {
  const message = JSON.parse(payload.body);
  // Hiển thị message ra UI
});
```
        "id": 2,
        "fullName": "User 2",
        "email": "user2@shop.com"
      }
    }
  ]
}
```

---

## Tóm tắt:
- Đăng nhập lấy token
- Init chat lấy info
- Kết nối WebSocket với token
- Subscribe topic
- Gửi/nhận tin nhắn qua STOMP
- Lấy danh sách cuộc trò chuyện qua REST API

## Tham khảo code mẫu (chat_admin.html)
- Đăng nhập, lấy token:
  ```js
  fetch('/api/auth/login', { ... })
  // Lưu access_token
  ```
- Kết nối WebSocket:
  ```js
  const socket = new SockJS('http://localhost:8080/ws-chat');
  const stompClient = Stomp.over(socket);
  stompClient.connect({ Authorization: 'Bearer ' + accessToken }, ...);
  ```
- Subscribe và gửi tin nhắn:
  ```js
  stompClient.subscribe('/user/queue/private', onMessageReceived);
  stompClient.send('/app/chat.sendPrivate', {}, `${receiverId}_${message}`);
  ```
- Nhận tin nhắn:
  ```js
  function onMessageReceived(payload) {
    // Xử lý tin nhắn
  }
  ```
