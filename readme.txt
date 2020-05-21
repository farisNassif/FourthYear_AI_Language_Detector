Faris Nassif | G00347032 | Supplimentary AI Assignment

[*]Running the Project Overview

   Running the jar will display a menu allowing you to,

   [1] Access Neural Network Options (Create, save, load, test once created etc)
   [2] Vector Hashing / K-mer Options (Choose vector / k-mer paramaters to generate the data file)
   [3] Classification Options (Only available after a NN + Vector is created, can classify files/strings)

   To perform tests against the test data or to classify input, you need to configure a vector for the session and generate a neural network 
   (either load one you previously made or create one to use in this session or save it for later).

   (*) Reccommended configuration input - [Vector Size -> 310], [K-mer Size -> 2]
   (*) The jar can be ran with -> java -cp .\language-nn.jar ie.gmit.sw.Runner

[*]Vector Hashing / K-mer Approach

   Given a input file or string, k-mers of size (n) are generated from that input. For each k-mer, a vector of size (m) is incrementally populated 
   by obtaining the index of the vector (hashcode of the k-mer modulo (m)) and incrementing it by 1 for each hit. Vector hashing is great here
   because it's a nice solution to help address the curse of dimensionallity, if there are more features than observations, theres a good risk 
   of overfitting the model, making vector hashing a nice inclusion.

   When generating the vector, the wili language dataset is processed line by line. Each line uses a 'clean' vector of fixed size (m). At the end
   of the line being processed, the vector is normalized between (0-1) and written to the csv file. Each line being processed will add a new line to 
   the csv file. If the vector is a size of (300), there will be 300 columns in the csv file for each line, followed by 235 columns for classification. 
   234 columns will contain a '0', while the column which indicates the language the data on that row belongs to will be flagged with a '1'.

   Once I got the vector processor working, I started playing around with feeding different sized vectors populated with different sized k-mers
   to the neural network. I found the sweet spot in terms of vector size to be around 310, a size too low I found to not be discriminative
   enough for training, and a large vector size to be too noisy, there were clearly issues during training when processing a vector of size 700+,
   as the NN would reach a point where the diminishing returns would be too great, even including varying rates of dropout didn't seem to curb
   the overfitting.

   One thing I did observe was the impact of normalizing the data. For the hidden layer I'm using the hyperbolic tangent function, so I thought 
   normalizing the data between (-1, 1) would be better, but as it turned out, after tweaking it to (0,1), the accuracy of training increased 
   by about 7%. I really wasn't sure (and still not really sure) why that was the case, the only thing I can think of was that maybe the bigger
   range made the data noisier than it needed to be?

   *As a sidenote, when running the program, the vector must be generated first before the NN, since the csv file for training the NN will only be 
   present once the vector is created.

[*]Neural Network Node Size / Activation Topology / Functions

   After a lot of chopping and changing, the Neural Network was ultimately constructed with a three tier topology. The nodes in the first (input) 
   layer aren't fixed since each node in the input layer should be mapped to each data column in the vector. After running the jar and depending 
   on what the size of your vector was, you'll be asked to input the amount of nodes (should be equal to the vector size, reccomendations will be 
   given when creating components via the menu). 

   Unlike the input nodes, the nodes in the single hidden layer are more flexible. Using the geometric pyramid rule (sqrt(input_nodes, output_nodes)),
   hidden nodes are created on the fly. I found it to be a nice addition instead of having something extra that I needed to account for each time I
   changed up the vector size, as everytime I changed the nodes in the input layer I'd have to make sure the amount of hidden nodes weren't causing
   overfitting/underfitting, where as the heuristic could comfortably handle the issue of hidden nodes on the fly.

   No matter how many times I tried and tested adding an additional layer, the results always showed the addition to be a negative one. After doing
   a bit of searching, the vanishing gradient problem seemed the likely problem, gradually decreasing the scope to a very small value, making training
   a lot more difficult than it needs to be. With the project itself, the network doesn't need to be 'deep', the biggest limiting factor of training
   I think is the lack of data. After playing around with bigger files, the accuracy seemed to shoot up beyond 94% in very little time, compared to being
   lucky to see 94% after 10 minutes with my final configuration.

   The output layer is the only really 'fixed' layer, containing 235 nodes, one for each potential language classification. The sole job of the output 
   layer is to classify the language based on input data.
   
   The final topology is as follows: 
   
   [Input Layer ] ->  ActivationReLU    -> Nodes=(Vector.size)
   [Hidden Layer] ->  ActivationTANH    -> Nodes=(Sqrt(Vector.size, 235)) -> Dropout(4135)
   [Output Layer] ->  ActivationSoftMax -> Nodes=(235)

   The input layer doesn't really need an activation function, but I stuck one in for the craic. It literally just recieves the raw data and doesn't
   require a function.

   Between the input and output layer consists of a single hidden layer. The nodes are calculated using the geometric pyramid rule and it incorporates 
   the TANH activation function which uses the hyperbolic tangent function. The TANH activation function has a derivative, meaning it can be used for 
   propagation training unlike BiPolar activation. Before trialing TANH, I tried almost every other activation, ReLU, Gaussian, LOG and many many more. 
   TANH seemed superior to the other activation functions from trials, since they showed more resistance to training when getting down to the lower 
   error rates, giving diminishing returns and poor iteration times. With TANH, I was able to increase my training accuracy from 75% over 3 minutes 
   to 85%-88%

   Following the advise of classmates, I included some dropout in the hidden layer. I wasn't seeing any significant results with small dropout amounts,
   it wasn't until I started testing at the 4000-4200 dropout range I found there to be a 1% - 2% increase in training accuracy. Dropout works by randomly
   'dropping' out nodes, essentially periodically removing nodes from the network along with their incoming and outgoing connections. It can help to 
   address overfitting, but the dropout rate can be volatile from what I've seen, too much and it can punish the accuracy.

   The output layer uses the SoftMax activation function. This means when the neural network is asked to process a vector for classificaiton, it'll spit out
   235 different outputs (one for each node/language), based on the confidence of each node being a specific language, it'll be given a value. It does this
   for all 235 nodes, then those values are normalized between 0 and 1, node with the highest value is the classified language. 
   
      

   

