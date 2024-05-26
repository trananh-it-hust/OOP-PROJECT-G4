import pandas as pd # type: ignore
import os
import json

original_dir = os.getcwd()
def find_search_engine_path():
    # Lấy đường dẫn tuyệt đối của thư mục hiện tại
    current_directory = os.getcwd()

    # Duyệt qua tất cả các thư mục và tệp tin trong cây thư mục bắt đầu từ thư mục hiện tại
    for dirpath, dirnames, filenames in os.walk(current_directory):
        # Kiểm tra xem thư mục hiện tại có tên là "search_engine" không
        if "search_engine" in dirnames:
            return os.path.join(dirpath, "search_engine")
        
    # Trả về None nếu không tìm thấy thư mục "search_engine" trong cây thư mục
    return None
os.chdir(find_search_engine_path())

#đọc file csv
relative_path = "../../resources/data/all_data.csv"
preprocessed_path = "../../resources/data/preprocessed_data.csv"
df = pd.read_csv(relative_path)
preprocessed_df = pd.read_csv(preprocessed_path)

os.chdir(original_dir)


def get_data_stats(df, pre_df=preprocessed_df):
    # Đọc file CSV vào DataFrame
    
    # Tổng số hàng trong CSV
    article_count = len(df)
    
    # Tổng hợp số lượng bài viết theo tác giả
    authors = df[' Author'].value_counts().to_dict()
    
    # Tổng hợp số lượng bài viết theo nguồn website
    website_sources = df[' Website source'].value_counts().to_dict()
    
    # Tổng hợp số lượng bài viết theo category
    categories = {}
    for category in pre_df[' Category']:
        for cat in str(category).split(' '):
            cat = cat.strip()
            if cat in categories:
                categories[cat] += 1
            else:
                categories[cat] = 1
    
    # Tổng hợp số lượng bài viết theo tags
    tags = {}
    for tag in pre_df[' Tags']:
        for t in str(tag).split(' '):
            t = t.strip()
            if t in tags:
                tags[t] += 1
            else:
                tags[t] = 1
    
    # Tạo từ điển kết quả
    result = {
        "articleCount": article_count,
        "website_source": website_sources,
        "authors": authors,
        "category": categories,
        "tags": tags
    }
    
    # Chuyển đổi từ điển kết quả thành JSON
    result_json = json.dumps(result, indent=4)
    
    return result_json