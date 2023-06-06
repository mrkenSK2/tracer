# tracer
## X4-1
基礎課題に続く形でコールグラフを作成しました。エージェント実行コマンドで`callGraph.dot`が`build`直下に作成されます。画像の生成にはさらにコマンドを実行してください。

*実行方法*
```
java -javaagent:build/libs/tracer-all.jar -cp classpath class
```

*PNG*
```
dot -Tpng -o path build/callGraph.dot
```
*SVG*
```
dot -Tsvg -o path build/callGraph.dot
```
