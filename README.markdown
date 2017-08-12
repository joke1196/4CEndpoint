## diff Endpoint



To launch the application:

run ```sbt run```

The application will be available on the port 8080 of localhost

To lauch the test:

run ```sbt test```

The application will respond to three endpoints :

-GET {domain}/v1/diff/{id}

-PUT {domain}/v1/diff/{id}/right

-PUT {domain}/v1/diff/{id}/left

id being an Int

The payload passed with the PUT method should be Json data with the form :

```{"data": "{string}"}```

with string being a Base64 encoded string