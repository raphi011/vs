Reflect about your solution!

Summary:

Every feature should be implemented fairly correctly. There is a big focus on reuseable code, client and server
both use the same channel / protocol / connection interfaces and (abstract) classes to communicate with eachother.

Most request / responses are sent in the format COMMAND|RESULTCODE|PARAMS?|MESSAGE?
