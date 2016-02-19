# [MeuralGen]

### Inspiration
- MeuralGen was first thought of as a logical extension of the famous Google Deepdream experiment, which has since grown into DeepArt and DeepStyle, which allow anyone to generate stylized art based on any image, photo, or painting. Seeing many other implementations of polyphonic generational techniques using complex methods, I wondered if it would be possible to generate convincing music through simpler means.

## What it does
- MeuralGen generates music through utilizing the characteristics of Recurrent Artifical Neural Networks with Long Short Term Memory. By amassing data from specific composers, MeuralGen is capable of generating music in certain composers' styles. Currently, MeuralGen holds one network trained by Frederick Chopin's piano pieces.

## Mechanism / Method
- Going off the notion that "Music itself is a language," I worked to parse MIDI files, which store music as instructions (not unlike sheet music) into a form understandable to a language-predicting neural network (see karpathy's char-rnn). I programmed the Java-based "Midi Num(eric) Converter" to convert MIDI files into a textual form informed by the nature of deep learning - that is, a textual form emphasizing crucial data and minimizing extraneous distractors. With data amassed from a compilation of Chopin piano works, I was able to train the Long Short-Term Memory Recurrent Neural Network (LSTM RNN) presented by Karpathy to produce textual representations which could be translated through my Converter into MIDI files, and thus, music.

## Challenges
- As the parameters I sought to mark down in the converted MIDI were not well-expressed in the MIDI files themselves, there were a few obstacles in the writing of the MIDI Converter both from MIDI to text and vice versa. As well, since Torch-generated text representations would still occasionally produce errors, some ambiguity-handling capability was needed in the text-to-MIDI converter.         

- Separately, since karpathy's [char-rnn] is written in Torch, a scientific computing library for Lua which is unfortunately not compatible with Windows, setting up a working environment proved extremely troublesome. Even then, the two major halves of this project (The Java MIDI Converter and the Neural Network) are separated across OS and intermediate steps must be performed manually.

- At a point, I decided that the music seemed too "random" and attributed this to the representation of key as an absolute number. However, an attempt to train and sample a network model produced from MIDIs parsed with "relative numbers" (that is, intervals) failed spectactularly as the neural network enthuiastically added intervals until notes became out of range in both directions. The true solution was to decrease the "temperature" variable of the network sampling script.

## Accomplishments
- The real production of actual, polyphonic music with an amount of structure and recognizable style through such a simple method as a character-based Recurrent Neural Network felt like a very unlikely goal - and thus, the realization of that is really rather amazing.

## Lessons learned
- Firstly: The way in which information is presented to a machine learning algorithm can be as important as the algorithm itself.

- Secondly: The most obvious and easiest solution sometimes still might be the best one.

- Thirdly: "Powerful" does not mean "error-free." Code for interpreting unpredictable inputs should be extremely robust.

## What's next

- Elimination of intermediate steps between network preparation and music generation
- More data sets from a diverse variety of distinct composers
- Non-command line GUI
- More exploration of alternative data expression

# Usage

### To generate music

Download a .t7 file from the results folder in the repository, in either the absolute (recommended) or relative folders, and move it into the cv folder in char-rnn-master.

Navigate to the location of char-rnn-master and call sample.lua according to instructions by [karpathy]. Use ```>> [filename.txt]``` after the file to output the console to a text file. Then, after retrieving the text file, run the MIDI Converter under option 3 and enter the arguments as requested. It is advised to convert the output MIDI to a MP3 through a program capable of editing tempo.

Altering the "temperature" variable may assist in producing more or less chaotic outputs.

   [char-rnn]: <https://github.com/karpathy/char-rnn>
   [karpathy]: <https://github.com/karpathy/char-rnn>
   [MeuralGen] <https://dl.dropboxusercontent.com/u/26075333/%5BWorks%5D/Music/MusGen/generated.html>