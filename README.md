# mock_burner
Tool for create and use mock server

The idea is simple:
In config.json set address of the original server.
In mobile app sets the address of the machine on which the mock_burner is launched.
The logic of work - if there is no such request saved - the application makes a request to the server and saves the answer, also giving it to the mobile application.
If there is a saved answer, it returns it from the file.
You can change the saved responses to achieve the desired state of the mobile app.

Now proxy body, headers, queries. Cookie not support.
