# External API

:warning: **!PLEASE READ!** :warning:

Few weeks after the Car Service API was developed, the external API it was using to fetch car manufacturer and model
names was removed. To make the Car Service API usable, I have luckily saved the data it was returning and decided to
make this dummy `WebServer` in [Python 3](https://www.python.org/downloads/) that would return the necessary data.

The `WebServer` is very simple and _"exposes"_ only one endpoint:

- CARS ENDPOINT - used to obtain car manufacturer and model names that are used to validate car names in my Car Service
  API.

## Usage :hammer:

The most simple setup you have ever seen: run the following command in the current directory, and you're all set to
go. :wink:

```shell
$ python3 -m http.server 8008
```
