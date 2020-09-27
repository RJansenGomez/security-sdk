# Security SDK
This SDK aims to avoid repeating code through multiple projects that uses the same JWT, wants a limited session
and want to share the same User information. i.e Rols, Username, etc..

# Mandatory properties
    spring.redis.host -> url of redis server
    spring.redis.port -> redis port
    spring.redis.password -> in case of security
    session.minutes -> redis and JWT TTL
    secret.jwt -> recomendatios of the use a keystore for this information
    
# How to use it

* It has by default a basic configuration. If wanted you can override it for own customization. i.e white listed urls,auth protocol, etc...

    ```@Override
    protected void configure(HttpSecurity httpSecurity) {
        httpSecurity.csrf().disable()
                    .authorizeRequests().anyRequest().authenticated();
            // Add a filter to validate the tokens with every request
            httpSecurity.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
* To use it internally

    Be aware you must have a login method because the security filter checks for a Redis content when a JWT is received.
    
    To make a login following steps must be done:
    
    - Validate the user information with the stored
    - (Optional) set the user in the securityWrapper
    - Create the JWT with the user
    - Store the user in the cache
    
    ```
    public String login(UserLogin userLogin) {
        UserLogin userLogged = repository.login(userLogin);
        wrapper.setLogin(userLogged.getSecurityUser());
        String jwt = encryptor.createJWT(userLogged.getSecurityUser());
        securityCache.storeUser(userLogged.getSecurityUser());
        return jwt;
    }
     ```
  
  To get the current user based on the JWT
   ```
    SecurityWrapper.getCurrentUser();
    ```
  
# Testing

#### Unitary tests
    Just use mockito with the SecurityWrapper.
    @Mock
    private SecurityWrapper wrapper;
    
#### Component/Functional tests
 There is a feature with works with testsContainers/ test Redis Server. At the end a server.
    
    LoginMock class: provides a few methods that "mocks" a login and returns a JWT to be used in the Headers of the requests. 
    They can be overloaded to accept rols, or whatever.
    - mockLogin
    - mockLoginExpiredJWT
    - mockLoginSessionExpired
## License
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat-square)](http://badges.mit-license.org)
- **[MIT license](http://opensource.org/licenses/mit-license.php)**
