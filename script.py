import urllib.request, json
try:
    req = urllib.request.Request('http://localhost:8080/api/v1/auth/register', data=b'{"name":"Test","email":"py2@test.com","password":"password123","role":"INSTRUCTOR"}', headers={'Content-Type': 'application/json'})
    print('Register:', urllib.request.urlopen(req).getcode())

    req2 = urllib.request.Request('http://localhost:8080/api/v1/auth/login', data=b'{"email":"py2@test.com","password":"password123"}', headers={'Content-Type': 'application/json'})
    resp2 = urllib.request.urlopen(req2)
    print('Login:', resp2.getcode())
    token = json.loads(resp2.read())['data']['token']

    try:
        urllib.request.urlopen('http://localhost:8080/api/v1/users/me')
    except Exception as e:
        print('Me (No JWT):', e.code)

    req3 = urllib.request.Request('http://localhost:8080/api/v1/users/me', headers={'Authorization': 'Bearer ' + token})
    print('Me (JWT):', urllib.request.urlopen(req3).getcode())
except Exception as ex:
    print('Error:', ex)
