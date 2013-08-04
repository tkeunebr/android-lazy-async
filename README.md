Android lazy async
==================

If you are an Android developer, you most certainly have already dealt with AsyncTasks.

If you are a good Android developer, you most certainly know how difficult it is to properly manage
these tasks, from avoiding the leak of the Activity/Fragment container to correctly responding to configuration changes,
such as when the user rotates the device.

Android lazy async is a small library that aims at freeing you from these constraints while still allowing you to be flexible.

Usage
==================

The initial setup is extremely easy.

First, you need to create a UI-less Fragment that will hold on your background task by extending AsyncFragment:

    public class SampleTaskFragment extends AsyncFragment {
    }

You will then need to create an inner class in that Fragment, extending the internal FragmentTask class. The only method you need to override
in the classic doInBackground(), when you do stuff in the background. If you're familiar with Android's AsyncTask API, you already know what I'm talking about.

    class SampleTask extends FragmentTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // Do stuff in the background
        }
    }

The only method you need to write is execute(). You need to instantiate you custom AsyncTask and call its exec() method. That's it!

    @Override
    protected void execute() {
        new SampleTask().exec();
    }


Your Activity or Fragment container, which will launch and receive the result of the background task, will instantiate and hold
a reference to your TaskFragment in your onCreate() by calling

    taskFragment = AsyncFragment.get(getFragmentManager(), SampleTaskFragment.class);
    taskFragment.setCallbacks(this);

It will also need to implement the AsyncCallback interface.

    public class QueryFragment extends Fragment implements AsyncFragment.AsyncCallbacks<Void, Integer> {
    }

overriding the common onPreExecute(), onPostExecute(), etc. callbacks. For convenience, you can use the SimpleAsyncCallbacks
class and pass a reference to the setCallbacks() method.

Finally, you will start your background task by calling

    taskFragment.startNewStart();
    
And you'll be good to go! If you rotate the device, you task will still be running and the callbacks will be executed
in your newly created Context. The library also automatically handles canceling tasks that are no longer valid.

If you have any suggestion, don't hesitate to open a PR. I'll have a close look at it! Thanks.

Further information
==================

You can look at the sample code for a more complete implementation of the API. It features examples with both Activity and Fragment
container.

If you are using the support library and ActionBarSherlock, you can

    import fr.tkeunebr.androidlazyasync.acl.*
instead of

    fr.tkeunebr.androidlazyasync.*

If you are looking for a real world app using this library, you can have a look at [UVweb's source code](https://github.com/uvweb/UVwebForAndroid) which
uses it extensively.


Download
==================

You can download the [latest JAR](https://github.com/tkeunebr/android-lazy-async/raw/master/android-lazy-async-sample/android-lazy-async-sample/libs/android-lazy-async.jar)
and add it to your build path.

License
================

    Copyright 2013 tkeunebr.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
