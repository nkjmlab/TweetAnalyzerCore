TweetAnalyzer
=============
## H2データベースの準備
- lib/h2w.batを起動する．JDBC URLに，"jdbc:h2:tcp://localhost/./tweets"を指定すると，lib/以下にtweets.mv.dbというデータベースファイルが出来る．
![](http://i.gyazo.com/2a4a40c7188d7d3421ba84a9bb5e4cbf.png)
- data/init.sqlの中身を「H2コンソール」に入力して実行する．

## Eclipse上でのツイート取得プログラムの起動
- Eclipseを起動し「ファイル→インポート→既存プロジェクトをワークスペースへ」を選び，このディレクトリを指定する．
- Config.javaに前項で指定したJDBC URLを設定する．また，Config.javaにaccessToken, accessTokenSecret, consumerKey, consumerSecretを設定する．
- TweetsCrawler.javaを起動すると「巨人」という単語が含まれるツイートを探索するようになっている．適宜，自分の好きなキーワードに変更する．
- H2コンソールで「SELECT * FROM TWEEETS;」とすると集計したツイートを閲覧することができる．![](http://i.gyazo.com/77eb34d367f2c7965edcb6262eb630fe.png)
