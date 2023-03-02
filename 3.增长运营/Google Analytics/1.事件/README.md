# GA4 中的事件

# 自动收集的事件

「自動收集的事件」如其名就是在完成 GA4 設置後，GA4 會自動預設收集的事件。只要你使用的是 Google Analytics for Firebase SDK 或 gtag.js，就不必另外撰寫程式碼即可收集這些事件。以下也列出常用事件給大家參考，其餘更多自動收集的事件可參考 [Google 官方說明](https://support.google.com/analytics/answer/9234069?hl=zh-Hant&ref_topic=9756175)。

- Web（gtag.js）

  - First_visit（初次造訪）
  - Session_start（工作階段）
  - Page_view（瀏覽頁面）
  - User_engagement 用户参与度

- App（SDK） 应用程序（SDK）

  - First_open 首先打开
  - Dynamic_link_app_open
  - Screen_view 屏幕视图
  - User_engagement 用户参与度

# 加強型評估事件

「加強型評估事件」可用來評估使用者與你的內容互動之情況。不需要修改任何程式碼，只要為網站資料串流開啟「加強型評估」功能，GA4 就會立即開始傳送收集事件。如何開啟「加強型評估」的方式，就請大家回頭參考[這篇文章](https://blog.shopline.tw/google-analytics-4-instruction-part1/#-shopline-ga4)。以下同樣列出常用加強型評估事件供大家參考，其餘更多加強型評估事件細節可參考 [Google 官方說明](https://support.google.com/analytics/answer/9216061?hl=zh-Hant&ref_topic=9756175)。

- Scroll（捲動網頁到 90%）
- View_search_result（站內搜尋） View_search_result（站内搜索）
- Click
- Video_start 视频开始
- Video_progress
- `Video*complete 视频*完成`
- File_download 文件下载

# 建議事件

「建議事件」是在網站或行動應用程式中加入這些事件，有助你評估額外功能和行為，並產生更實用的報表。這類事件需要搭配其他相關背景資訊進行評估才會有意義，因此不會自動傳送。SHOPLINE 做為電商品牌的好夥伴，已預設好針對「電商」的建議事件，以下為大家整理說明，而關於其他場景的應用還請大家自行參考 Google 官方說明。

- View_item_list（瀏覽商品清單）
- View_item（瀏覽商品）
- Select_item（點擊商品清單資訊）
- Add_to_wishlist（將商品加入收藏）
- Add_to_cart（加入購物車）
- View_cart（瀏覽購物車頁面）
- Begin_checkout（開始結帳）
- Add_shipping_info（結帳時增加運送資訊）
- Add_payment_info（結帳時增加付款資訊）
- Purchase（完成購買）
- Search（搜尋）
- Sign_up（註冊）
- Refund（退款）
- Remove_from_cart（移除購物車商品）
- View_promotion（瀏覽促銷訊息）
- Select_promotion（點擊促銷訊息）

# 自訂事件

「自訂事件」如其名，就是指名稱和參數由你自行定義的事件，可收集你特別重視的相關資訊。但基本上前面上述介紹的自動收集的事件、加強型評估事件、建議事件就已可囊括多數用戶的追蹤需求，在此就不多做說明列舉，若有興趣還請大家自行參考 Google 官方說明。
