# DailyReward
## Description
서버에 출석체크 기능을 추가할 수 있게 해주는 플러그인 입니다.
### Use Spigot Version:
1.19.2

### Tested Spigot Version:
1.19.2

### Required Plugin:
X

## Install Guide
1. 릴리즈된 최신 버전의 플러그인 파일을 다운로드 합니다.  
2. 다운로드한 DailyReward.jar 파일을 플러그인 디렉토리에 저장합니다.

## Feature
### 출석체크 GUI 커스텀 가능
플러그인이 첫 활성화 되었을 때 생성되는  
rewards.yml 파일에서 사용자가 직접 출석체크의 GUI를 커스텀할 수 있습니다.  

rewards.yml의 양식은 다음과 같습니다.
```
Rewards:
  day1: #day(1~54)
    slot: 10 #0~53
    name: "&a1일차 보상" #GUI 아이템의 이름
    item_type: "EMERALD" #GUI 아이템의 타입
    lore:
      - "&e%rewards_receipt_status%" #%rewards_receipt_status% - 플레이어 데이터 파일값에 따라 바뀌는 아이템 수령 여부
      - "&e보상 목록 :" #GUI 아이템의 로어
      - "&f철괴 5개"
      - "&f금괴 5개"
    commands:
      - "give @s minecraft:iron_ingot 5" #플레이어가 클릭했을때 실행되는 명령어, /는 빼고 입력.
      - "give @s minecraft:gold_ingot 5"
```
rewards.yml 사용 예제 (해당 예제는 플러그인 첫 실행시에 적용되어있습니다.) :
```
Rewards:
  day1: 
    slot: 10 
    name: "&a1일차 보상"
    item_type: "EMERALD"
    lore:
      - "&e%rewards_receipt_status%"
      - "&e보상 목록 :"
      - "&f철괴 5개"
      - "&f금괴 5개"
    commands:
      - "give @s minecraft:iron_ingot 5"
      - "give @s minecraft:gold_ingot 5"
  day2:
    slot: 11
    name: "&a2일차 보상"
    item_type: "EMERALD"
    lore:
      - "&e%rewards_receipt_status%"
      - "&e보상 목록 :"
      - "&f철괴 10개"
      - "&f금괴 10개"
    commands:
      - "give @s minecraft:iron_ingot 10"
      - "give @s minecraft:gold_ingot 10"
  day3:
    slot: 12
    name: "&a3일차 보상"
    item_type: "EMERALD"
    lore:
      - "&e%rewards_receipt_status%"
      - "&e보상 목록 :"
      - "&f철괴 20개"
      - "&f금괴 20개"
    commands:
      - "give @s minecraft:iron_ingot 20"
      - "give @s minecraft:gold_ingot 20"
  day4:
    slot: 13
    name: "&a4일차 보상"
    item_type: "EMERALD"
    lore:
      - "&e%rewards_receipt_status%"
      - "&e보상 목록 :"
      - "&f철괴 30개"
      - "&f금괴 30개"
    commands:
      - "give @s minecraft:iron_ingot 30"
      - "give @s minecraft:gold_ingot 30"
  day5:
    slot: 14
    name: "&a5일차 보상"
    item_type: "EMERALD"
    lore:
      - "&e%rewards_receipt_status%"
      - "&e보상 목록 :"
      - "&f철괴 30개"
      - "&f금괴 30개"
      - "&f다이아몬드 5개"
    commands:
      - "give @s minecraft:iron_ingot 20"
      - "give @s minecraft:gold_ingot 20"
      - "give @s minecraft:diamond 5"
  day6:
    slot: 15
    name: "&a6일차 보상"
    item_type: "EMERALD"
    lore:
      - "&e%rewards_receipt_status%"
      - "&e보상 목록 :"
      - "&f철괴 30개"
      - "&f금괴 30개"
      - "&f다이아몬드 10개"
    commands:
      - "give @s minecraft:iron_ingot 20"
      - "give @s minecraft:gold_ingot 20"
      - "give @s minecraft:diamond 10"
  day7:
    slot: 16
    name: "&a7일차 보상"
    item_type: "EMERALD"
    lore:
      - "&e%rewards_receipt_status%"
      - "&e보상 목록 :"
      - "&f철괴 30개"
      - "&f금괴 30개"
      - "&f다이아몬드 15개"
    commands:
      - "give @s minecraft:iron_ingot 20"
      - "give @s minecraft:gold_ingot 20"
      - "give @s minecraft:diamond 15"
```

### 플레이어 데이터 파일 관리
플레이어 데이터 파일은 플레이어가 첫 접속할 시 plugin/Dailyreward/Players 폴더에  
플레이어의 UUID를 이름으로 하는 YAML 파일이 생성됩니다.  

(플레이어 UUID).yml 파일의 양식
```
CumulativeDate: 1
LastJoinDate: {}
ReceivedRewards: {}
```
CumulativeDate는 플레이어의 누적 접속일이며, 보상의 일차보다 누적 접속일이 적을시에는  
플레이어는 그 보상의 일차를 획득할 수 없습니다.  

LastJoinDate는 플레이어의 마지막 접속일이며 플레이어가 접속을 했을때 마지막 접속일이
접속날과 다를시에 CumulativeDate 값이 1씩 늘어납니다.

ReceivedRewards는 플레이어가 획득한 보상들입니다.

만약 모든 플레이어의 데이터를 삭제하고 싶을 때는  
plugin/Dailyreward/Players 폴더를 통째로 삭제하는식으로 관리할 수 있습니다.
