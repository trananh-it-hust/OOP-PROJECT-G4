# Bài tập lớn môn OOP nhóm 4
# MainController

## Các Phương Thức Chính

### `switchToMain(ActionEvent event)`

Phương thức này được gọi khi người dùng muốn chuyển đến trang chính của ứng dụng.

### `switchToSearchResults(Event event)`

Phương thức này được gọi khi người dùng thực hiện tìm kiếm và muốn chuyển đến trang kết quả tìm kiếm.

### `handleSearch()`

Phương thức này xử lý việc tìm kiếm các mục theo từ khóa nhập vào từ người dùng.

### `addSuggestions(List<String> suggestions)`

Phương thức này được sử dụng để thêm các gợi ý tìm kiếm vào giao diện người dùng.

### `initialize()`

Phương thức này được gọi khi controller được khởi tạo, và thiết lập các lắng nghe sự kiện cho các thành phần giao diện như `TextField`.

# Package com.oop.model

Gói này chứa các lớp mô hình cho ứng dụng.

## Item
### Constructor

- `Item()`: Constructor mặc định.
- `Item(String articleLink, String websiteSource, String articleType, String articleTitle, String content, Date creationDate, String author, String category, String tags, String summary)`: Constructor với các tham số để khởi tạo các thuộc tính.

### Phương Thức

- `readItemsFromCSV()`: Phương thức tĩnh để đọc các mục từ một tập tin CSV nằm tại `src/main/resources/data/data.csv`. Nó trả về một danh sách các đối tượng `Item`.
- `renderUI(Item item)`: Phương thức tĩnh để hiển thị giao diện người dùng cho một mục cụ thể. Hiện tại, nó tạo một VBox chứa một nhãn với tiêu đề bài viết.

### Getters và Setters
## SearchModel

Lớp `SearchModel` cung cấp các phương thức để thực hiện tìm kiếm và đề xuất cho các mục trong ứng dụng.

### `searchByTitle(List<Item> items, String keyword)`

Phương thức này thực hiện tìm kiếm các mục dựa trên tiêu đề của chúng. Nó trả về một danh sách các mục có tiêu đề chứa từ khóa tìm kiếm.

### `GetSuggestion(String keyword)`

Phương thức này trả về các gợi ý dựa trên từ khóa tìm kiếm đã cho. Hiện tại, nó trả về một danh sách các từ khóa gợi ý cứng cố định.


