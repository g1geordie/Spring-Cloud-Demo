# Spring Cloud Sample 
> This is a simple project allows you to view the basic spring cloud service .  
> Inlude Gateway , Discovery(Eureka) , Bus , Config , Feign , security(oauth2) , Admin 

---

## Follow step 
+  ### Start the server 

		gradle  SpringCloudRun 

+  ### stop the server 

>  control + C (Window doesn't work)  
>  shutdown service <http://localhost:9999/shutdown>  

> where gradle can't trigger shutdownhook in Windows.   


+ ### Find the service 

**you can use Eureka .**    
	Eureka Server : <http://localhost:8082/>  
	![SpringCloud -Eureka 畫面](pic/Eureka.png)


**or more powerful UI , Admin .**  
	Admin Server : <http://localhost:9000/>
	![SpringCloud -Admin 畫面](pic/admin1.png)
	![SpringCloud -Admin 畫面](pic/admin2.png)


+ ### try some link
	Gateway (app)   : <http://localhost:8080/app/></br>   
	Gateway (feign) : <http://localhost:8080/feign/></br>  
	Config          : <http://localhost:8081/app/bus/master/></br>  


---
## The microservice flow is like below  
![SpringCloud 畫面](pic/SpringCloud.png)


##Module Intro
preparing 