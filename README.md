# Final Project - *Pensum*

**Pensum** is an android app that allows a user to post tasks they need help with, bid on tasks, view and edit user profiles, as well as chatting with task candidates. The app utilizes [Parse](http://www.parse.com/) and [Zip Code API](https://www.zipcodeapi.com/).

Time spent: **3.5** weeks spent in total.

## User Stories

The following **required** functionality is completed:

* [x] User can **sign in to Facebook** using OAuth login
* [x] User can **view tasks from their dashboard timeline in grid view**
  * [x] User is displayed the **poster picture, name and task title, hashtag, and budget** 
  * [x] User is taken to **task detail** when they click a task
* [x] User can **view tasks from their dashboard timeline in map view**	
  * [x] User is displayed the **poster picture, name and task title, hashtag, and budget** when they click on a marker
  * [x] User is taken to the **task detail** when they click on an info window
* [x] User can **switch between portrait and landscape** mode
* [x] User can **post a new task**
  * [x] User can click a "New" Floating Action Button on the bottom right in the Dashboard
  * [x] User can then enter a new task and post it
  * [x] User is taken back to dashboard timeline after the new task is posted
  * [x] User can **view the new task** in "MY TASK"
* [x] User can **search for tasks** in Grid view
* [x] User can **apply search filters on task** in Grid view
* [x] User can view a list of of their tasks categorized by **open, accepted, completed** in "MY TASK menu
* [x] User can **delete their own task** by left swipe on a task
* [x] User can **edit an own task** if the status of the task is open
* [x] User can **accept a bid** on a task
* [x] User can **mark task as completed and rate the candidate** 
* [x] User can **view their own profile** by going to the "PROFILE" menu
* [x] User can **view any candidates profile** by clicking on their Profile image
* [x] User can **add, delete, and edit skills** on their profile
* [x] User can **view the rating and complete percentage** on a profile
* [x] As the task bidder, user can **view their bids** in "MY BIDS" menu
  * [x] User can **check the tasks they won** in won tab
  * [x] User can **check the tasks they got declined** in the declined tab
* [x] User can **view a list of tasks with bids and messages** by clicking on "MESSAGE" menu
* [x] As the task bidder, user can **bid on a task**
  * [x] User can click "contact" from detail view
  * [x] User can **enter a bid price and message** to task owner
* [x] As the task owner, user can **accept a bid on a task**
  * [x] User can click "accept" from detail view
  * [x] User is taken to the accept/decline screen after clicking the accept button
  * [x] User can **accept/decline a candidate** by swiping the candidate right or left
  * [x] User can click on the candidate to **chat with the candidate** in a message like system
* [x] As the task bidder, user can **chat with owner** in a message like system


The following **optional** features are implemented:

* [x] User can **upload a picture** on post a task page
* [ ] User can view more tasks as they scroll with [infinite pagination](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView). Number of tasks is unlimited.
* [ ] User can **pull down to refresh dashboard grid timeline**
* [ ] User can **open the app offline and see last loaded posts**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.
* [x] User can see embedded image within the post detail view
* [x] User can see embedded map within the post detail view
* [x] Apply the popular [Butterknife annotation library](http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife) to reduce view boilerplate.
* [x] [Leverage RecyclerView](http://guides.codepath.com/android/Using-the-RecyclerView) as a replacement for the ListView and ArrayAdapter
* [ ] Leverages the [data binding support module](http://guides.codepath.com/android/Applying-Data-Binding-for-Views) to bind data into layout templates.
* [ ] Replace Picasso with [Glide](http://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en) for more efficient image rendering.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

### FB Login and Dashboard
<img src='http://i.imgur.com/zJOZJNp.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

### Create a Task
<img src='http://i.imgur.com/Y1dIx9R.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

### Accept and Complete a Task
<img src='http://i.imgur.com/6y6T4EV.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

### Chat/Message a task author
<img src='http://i.imgur.com/29isJwX.gif' title='Video Walkthrough' width='300' alt='Video Walkthrough' />

### Attach picture to your task and delete own task
<img src='http://i.imgur.com/1iwTiss.gif' title='Video Walkthrough' width='300' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

TBD

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android


## License

    Copyright 2016 Daryl Halima, Eddie Tseng, Terri Chu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


icon by [Arun Thomas](https://www.iconfinder.com/arunxthomas) is licensed under [CC BY 2.0](http://creativecommons.org/licenses/by/2.0)
