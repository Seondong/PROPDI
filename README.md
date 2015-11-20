# PROPDI: Property Domain Inference Module,  Ver. Nov-2015  

#주요 사항
## 1. 2015년 1차 소프트웨어 통합(8월 22일)을 위한 B-Box 결과물
내용: 두 가지 종류의 타입찾기 알고리즘(Schema Based, Instance Based)을 다음 환경(Virtuoso I/O + Maven + Spring REST API) 에서 동작하도록 함

## 2. 소프트웨어 I/O
Config.ini에서 파라미터 수정 가능
기본적으로 가지고 있는 지식 베이스를 디비피디아2014라 놓고 진행함
Type Inference를 필요로 하는 트리플의 IRI: TEST_INPUT_IRI
알고리즘 실행 후, TEST_OUTPUT_IRI에 트리플 자동 저장

예) Virtuoso server (dmserver5.kaist.ac.kr)의 DBpedia 2014을 지식 베이스로 삼아, 2014년도 L-Box triple(IRI: lbox.kaist.ac.kr)를 Input으로 받고, 타입을 찾은 결과를 IRI: bbox.kaist.ac.kr 에 저장한다.


## 3. REST SERVICE 사용 방법

### 3.1. mvn clean package 를 실행한다.
정상적으로 실행된다면 target 폴더에 BBOX-{version}-SNAPSHOT.jar 파일이 생성된다.


### 3.2. REST SERVICE를 동작시킨다.
```
java -jar target/BBOX-{version}-SNAPSHOT.jar
```

### 3.3. 외부에서의 테스트 방법
#### 3.3.1 리눅스의 curl 명령어를 이용하는 방법
```
curl -H "Accept: application/json" -H "X-AUTH-TOKEN: XXXX" -X POST -d "[1234,4567,89012]" http://143.248.90.195:8080/BBOX2015
```


