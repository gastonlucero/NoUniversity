# README #

This README would normally document whatever steps are necessary to get your application up and running.

### Exercise curls ###
Exercise2:
curl 'http://localhost:8080/'
curl 'http://localhost:8080/ping'
curl 'http://localhost:8080/ping/numa'
curl 'http://localhost:8080/pingUrlParam?number=10'
curl --header "myHeader: hola numa" http://localhost:8080/pingHeader'

Exercise3:
curl  'http://localhost:8080/joined/users/byname?name=student1'
curl  'http://localhost:8080/joined/users/all'
curl  'http://localhost:8080/joined/users/paginated'
curl  'http://localhost:8080/joined/users/all?page=19'
curl  'http://localhost:8080/joined/services?id=15'
curl  'http://localhost:8080/joined/services'

Exercise4:
curl -H "Content-type: application/json" -X PUT -d '{"name": "numa1", "id":"1","lastName":"stratio","email":"numName@stratio.com"}'  'http://localhost:8080/composed/users/upsert'
curl -H "Content-type: application/json" -X POST -d '{"name": "numa2", "id":"2","lastName":"stratio","email":"numName@stratio.com"}'  'http://localhost:8080/composed/users/upsert'
curl 'http://localhost:8080/composed/users'
curl -X DELETE 'http://localhost:8080/composed/users/1'
curl  'http://localhost:8080/composed/services/upgrade?id=1&version=2.0.0'

Exercise5
curl  --header  'X-Real-Ip: 1.2.3.4' -v 'http://localhost:8080/advanced/cookie'
curl --header 'token: myToken' -v 'http://localhost:8080/advanced/token'
curl  'http://localhost:8080/advanced/token?token=myToken'

Exercise6
curl  'http://localhost:8080/actors'

Exercise7
curl  'http://localhost:8080/myHandlers/rejections?number=10'
curl -X POST 'http://localhost:8080/myHandlers/rejections?number=10'
curl 'http://localhost:8080/myHandlers/rejections?number=a'
curl 'http://localhost:8080/myHandlers/norejections?number=10'
curl 'http://localhost:8080/myHandlers/exception?number=10'

curl 'http://localhost:8080/myHandlers/breaker/1s'
curl 'http://localhost:8080/myHandlers/breaker/6s'
curl 'http://localhost:8080/myHandlers/breaker/1s'

### Assignments ###

