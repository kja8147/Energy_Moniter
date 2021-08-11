# Energy_Moniter
사이드 프로젝트

# 개요
  * 지구온난화가 가속화되면서 세계 곳곳에 폭염, 폭우 등 기상이변이 발생하고 있다.
  * 온난화의 심각성을 깨닫게 할 수있는 어플을 만들어야 겠다고 생각했다.
  * 개인이 할 수 있는 활동으로는 전력 소비량, co2 배출량 줄이기 등 이 있다고 생각했다.

# 개발기간 
  2021.08.04 ~ 2021.08.11
  
# 목표
  * 지구온난화의 심각성을 깨닫고 전력, co2 배출량을 줄이기
  
# 개발환경
  * 도구 : android studio
  * 라이브러리 : mpAndroidChart
  * 언어 : java

# 프로젝트 구성도


# 프로젝트 기능
  1. 전력 소비량 (시대별, 나라별, 해당 나라의 시대별)
  2. co2 배출량 (시대별, 나라별, 해당 나라의 시대별)
  3. 가전기기에 따른 소비전력과 co2 배출량
  4. 한국 가구전력 배출량 조회 서비스

# 스크립트 구조

# 주요 기술
  * json parsing :
  * exel :
  * chart :

# 결과

[메인화면]
![image](https://user-images.githubusercontent.com/48000920/129014336-5a43fd05-ed4b-4b5d-97d4-c669eebfe797.png)


[1. 전력 소비량 : global 버튼 누른경우]

국외 ver.

![image](https://user-images.githubusercontent.com/48000920/129014414-04b2510d-bbd3-481e-961a-bc12a46f36e7.png)
위 막대 그래프 중에 원하는 시대의 막대를 누르면 아래의 원 차트가 뜬다.
원 차트는 클릭한 연도별 전력 사용량을 나라순으로 나눈 것이다.
![image](https://user-images.githubusercontent.com/48000920/129014630-e8f56529-c4f1-446d-ad24-5197db6d48f1.png)

아래로 계속 스크롤 하다보면 나라별 전력 사용량 부분이 있다.
원하는 나라를 클릭하면 시대별로 전력 사용량을 볼 수 있다.
![image](https://user-images.githubusercontent.com/48000920/129014790-9852eaa8-2d64-4659-9d6e-8fe7abdc5cae.png)

![image](https://user-images.githubusercontent.com/48000920/129014948-00336fa2-585b-4a94-865c-e5a47eee662e.png)

다음은 국외가 아닌 국내 탭을 클릭하면 한국 가구평균 전력 사용량을 보여준다. 

국내 ver. 국외 탭에서 제공하는 기능과 같은 기능.

![image](https://user-images.githubusercontent.com/48000920/129015925-792bd31f-71ef-4670-8e71-6726e08d325b.png)
![image](https://user-images.githubusercontent.com/48000920/129016893-83fbf4f5-3b97-44e3-96cd-49c33516c0bd.png)


[2. co2 배출량]
![image](https://user-images.githubusercontent.com/48000920/129016967-4f45f8ef-cbea-47d2-bf6a-037ec6496792.png)
![image](https://user-images.githubusercontent.com/48000920/129016998-be5c34b5-28bc-40f5-a5b2-8d6e131a3e0d.png)
![image](https://user-images.githubusercontent.com/48000920/129017032-0d1c09ef-26d8-4c9c-bfd5-8676c19696d1.png)

[3. 가구별 연간 소비전력]
![image](https://user-images.githubusercontent.com/48000920/129017100-dff4b1aa-4339-485a-8fb7-01a724d5d8b0.png)

[4. 한국 전력 소비량 조회]
![image](https://user-images.githubusercontent.com/48000920/129017171-57461750-e1de-4107-84c1-5a1d256c500e.png)
![image](https://user-images.githubusercontent.com/48000920/129017215-8f00b96b-13a8-4c8f-b6cc-514d358745bb.png)
![image](https://user-images.githubusercontent.com/48000920/129017236-57d0d77b-fb6c-4d3b-867c-15cb26d4bdb8.png)


# 데이터 참조 사이트
  1. Enerdata (글로벌 전력, co2 데이터) : https://yearbook.enerdata.co.kr/electricity/electricity-domestic-consumption-data.html
  2. kepco (한국 가구 평균 전력 소비량) : https://bigdata.kepco.co.kr/cmsmain.do?scode=S01&pcode=000493&pstate=house&redirect=Y


