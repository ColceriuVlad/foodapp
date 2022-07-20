function fn() {
  karate.log('karate.env system property was:', env);
  if (!env) {
    env = 'local';
  }

  var env = karate.env; // get system property 'karate.env'

  var config = {
    env: env,
    myVarName: 'someValue'
  }

  if (env == 'dev') {
    config.baseUrl="http://localhost:8080";
  } else if (env == 'e2e') {
    config.baseUrl="http://localhost:8080";
  } else if(env='local'){
    config.baseUrl="http://localhost:8080";
  }
  return config;
}