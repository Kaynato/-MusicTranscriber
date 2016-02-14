# MeuralGen

### Inspiration
- MeuralGen was first thought of as a logical extension of the famous Google Deepdream experiment, which has since grown into DeepArt and DeepStyle, which allow anyone to generate stylized art based on any image, photo, or painting. Seeing many other implementations of polyphonic generational techniques using complex methods, I wondered if it would be possible to generate convincing music through simpler means.

## What it does
- MeuralGen generates music through utilizing the characteristics of Recurrent Artifical Neural Networks with Long Short Term Memory. By amassing data from specific composers, MeuralGen is capable of generating music in certain composers' styles. Currently, MeuralGen holds one network trained by Frederick Chopin's piano pieces.

## How I built it
- Going off the notion that "Music itself is a language," I worked to parse MIDI files, which store music as instructions (not unlike sheet music) into a form understandable to a language-predicting neural network (see karpathy's char-rnn). I programmed the Java-based "Midi Num(eric) Converter" to convert MIDI files into a textual form informed by the nature of deep learning - that is, a textual form emphasizing crucial data and minimizing extraneous distractors. With data amassed from a compilation of Chopin piano works, I was able to train the Long Short-Term Memory Recurrent Neural Network (LSTM RNN) presented by Karpathy to produce textual representations which could be translated through my Converter into MIDI files, and thus, music.

## Challenges I ran into
- As the parameters I sought to mark down in the converted MIDI were not well-expressed in the MIDI files themselves, there were a few obstacles in the writing of the MIDI Converter both from MIDI to text and vice versa. As well, since Torch-generated text representations would still occasionally produce errors, some ambiguity-handling capability was needed in the text-to-MIDI converter.         

- Separately, since karpathy's char-rnn is written in Torch, a scientific computing library for Lua which is unfortunately not compatible with Windows, setting up a working environment proved extremely troublesome. Even then, the two major halves of this project (The Java MIDI Converter and the Neural Network) are separated across OS and intermediate steps must be performed manually.

- At a point, I decided that the music seemed too "random" and attributed this to the representation of key as an absolute number. However, an attempt to train and sample a network model produced from MIDIs parsed with "relative numbers" (that is, intervals) failed spectactularly as the neural network enthuiastically added intervals until notes became out of range in both directions. The true solution was to decrease the "temperature" variable of the network sampling script.

## Accomplishments
- The real production of actual, polyphonic music with an amount of structure and recognizable style through such a simple method as a character-based Recurrent Neural Network felt like a very unlikely goal - and thus, the realization of that is really rather amazing.

## What I learned
- Firstly: The way in which information is presented to a machine learning algorithm can be as important as the algorithm itself.

- Secondly: The most obvious and easiest solution sometimes still might be the best one.

- Thirdly: "Powerful" does not mean "error-free." Code for interpreting unpredictable inputs should be extremely robust.

## What's next

- Elimination of intermediate steps between network preparation and music generation
- More data sets from a diverse variety of distinct composers


Markdown is a lightweight markup language based on the formatting conventions that people naturally use in email.  As [John Gruber] writes on the [Markdown site][df1]

> The overriding design goal for Markdown's
> formatting syntax is to make it as readable
> as possible. The idea is that a
> Markdown-formatted document should be
> publishable as-is, as plain text, without
> looking like it's been marked up with tags
> or formatting instructions.

This text you see here is *actually* written in Markdown! To get a feel for Markdown's syntax, type some text into the left window and watch the results in the right.

### Version
3.2.7

### Tech

Dillinger uses a number of open source projects to work properly:

* [AngularJS] - HTML enhanced for web apps!
* [Ace Editor] - awesome web-based text editor
* [Marked] - a super fast port of Markdown to JavaScript
* [Twitter Bootstrap] - great UI boilerplate for modern web apps
* [node.js] - evented I/O for the backend
* [Express] - fast node.js network app framework [@tjholowaychuk]
* [Gulp] - the streaming build system
* [keymaster.js] - awesome keyboard handler lib by [@thomasfuchs]
* [jQuery] - duh

And of course Dillinger itself is open source with a [public repository][dill]
 on GitHub.

### Installation

You need Gulp installed globally:

```sh
$ npm i -g gulp
```

```sh
$ git clone [git-repo-url] dillinger
$ cd dillinger
$ npm i -d
$ gulp build --prod
$ NODE_ENV=production node app
```

### Plugins

Dillinger is currently extended with the following plugins

* Dropbox
* Github
* Google Drive
* OneDrive

Readmes, how to use them in your own application can be found here:

* [plugins/dropbox/README.md] [PlDb]
* [plugins/github/README.md] [PlGh]
* [plugins/googledrive/README.md] [PlGd]
* [plugins/onedrive/README.md] [PlOd]

### Development

Want to contribute? Great!

Dillinger uses Gulp + Webpack for fast developing.
Make a change in your file and instantanously see your updates!

Open your favorite Terminal and run these commands.

First Tab:
```sh
$ node app
```

Second Tab:
```sh
$ gulp watch
```

(optional) Third:
```sh
$ karma start
```

### Docker, N|Solid and NGINX

More details coming soon.

#### docker-compose.yml

Change the path for the nginx conf mounting path to your full path, not mine!

### Todos

 - Write Tests
 - Rethink Github Save
 - Add Code Comments
 - Add Night Mode

License
----

MIT


**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [dill]: <https://github.com/joemccann/dillinger>
   [git-repo-url]: <https://github.com/joemccann/dillinger.git>
   [john gruber]: <http://daringfireball.net>
   [@thomasfuchs]: <http://twitter.com/thomasfuchs>
   [df1]: <http://daringfireball.net/projects/markdown/>
   [marked]: <https://github.com/chjj/marked>
   [Ace Editor]: <http://ace.ajax.org>
   [node.js]: <http://nodejs.org>
   [Twitter Bootstrap]: <http://twitter.github.com/bootstrap/>
   [keymaster.js]: <https://github.com/madrobby/keymaster>
   [jQuery]: <http://jquery.com>
   [@tjholowaychuk]: <http://twitter.com/tjholowaychuk>
   [express]: <http://expressjs.com>
   [AngularJS]: <http://angularjs.org>
   [Gulp]: <http://gulpjs.com>

   [PlDb]: <https://github.com/joemccann/dillinger/tree/master/plugins/dropbox/README.md>
   [PlGh]:  <https://github.com/joemccann/dillinger/tree/master/plugins/github/README.md>
   [PlGd]: <https://github.com/joemccann/dillinger/tree/master/plugins/googledrive/README.md>
   [PlOd]: <https://github.com/joemccann/dillinger/tree/master/plugins/onedrive/README.md>


