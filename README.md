## Getting started
There are two ways to install the app to the installation device:
1. Download the APK file from folder deliverables of the repository to the Android device and install it.
2. Connect the Android device with a computer, run the project source code of the app with Android Studio, and run the app on that device.
   Open the app after installing it, register or sign in and enjoy it.
   Note that this app is compatible with Android devices with API 32 or higher, it may not work on lower version devices.

## Database
### Audio files
| name				        | type		       | description                |
|:----------------|:-------------|:---------------------------|
| livechat				    | dictionary		 | live comments on this song |
| livechatNum				 | int			       | number of live comments    |
| playedTimes			  | int				      | number of playing          |
| likedTimes			   | int			       | number of "Like"           |

### Users
| name				     | type		     | description                |
|:-------------|:-----------|:---------------------------|
| Context				  | string		   | context of message         |
| Receiver				 | string			  | who got the message (Uid)  |
| Sender			    | string				 | who sent the message (Uid) |
| TimeStamp			 | long			    | time of the message        |

### Synchronized Playback room
| name				             | type		    | description                                 |
|:---------------------|:----------|:--------------------------------------------|
| currentMusicName				 | string		  | name of current playing music or audiobook  |
| playStatus				       | bool			   | whether this audio file is playing          |
| pos			               | int				   | if playing, what is current position        |
| repoName			          | string			 | where is this audio file stored in database |
| visitorStatus			     | bool			   | whether both users are in this room         |

### Reports
| name				         | type		    | description                                                                                                                |
|:-----------------|:----------|:---------------------------------------------------------------------------------------------------------------------------|
| isBlocked				    | string		  | the status of the user who is reported, administrator can change this value to punish that user if the report is confirmed |
| livecomments				 | string			 | the content which was reported                                                                                             |

## Implementation
[Data entity ](https://git.rwth-aachen.de/iptk/ws22-23/groupb/audiostreamapp/-/tree/main/app/src/main/java/com/example/audiostreamapp/data/model)

[Login](https://git.rwth-aachen.de/iptk/ws22-23/groupb/audiostreamapp/-/blob/main/app/src/main/java/com/example/audiostreamapp/LoginActivity.java)

[Admin mode](https://git.rwth-aachen.de/iptk/ws22-23/groupb/audiostreamapp/-/blob/main/app/src/main/java/com/example/audiostreamapp/AdminModeActivity.java)

[Message handler](https://git.rwth-aachen.de/iptk/ws22-23/groupb/audiostreamapp/-/blob/main/app/src/main/java/com/example/audiostreamapp/DirectMessageActivity.java)

[Profile handler](https://git.rwth-aachen.de/iptk/ws22-23/groupb/audiostreamapp/-/blob/main/app/src/main/java/com/example/audiostreamapp/DisplayProfileActivity.java)

[Favorite](https://git.rwth-aachen.de/iptk/ws22-23/groupb/audiostreamapp/-/blob/main/app/src/main/java/com/example/audiostreamapp/FavoriteActivity.java)

[Chat room](https://git.rwth-aachen.de/iptk/ws22-23/groupb/audiostreamapp/-/blob/main/app/src/main/java/com/example/audiostreamapp/LiveRoomActivity.java)

[Sync](https://git.rwth-aachen.de/iptk/ws22-23/groupb/audiostreamapp/-/blob/main/app/src/main/java/com/example/audiostreamapp/syncFunction/SyncRoomActivity.java)

[Live comment](https://git.rwth-aachen.de/iptk/ws22-23/groupb/audiostreamapp/-/tree/main/app/src/main/java/com/example/audiostreamapp/liveComment)

[Main fragment](https://git.rwth-aachen.de/iptk/ws22-23/groupb/audiostreamapp/-/tree/main/app/src/main/java/com/example/audiostreamapp/ui)