
# Ch 6. Deep Feedforward Networks


#### feedforward network

* is to **approximate** some mapping y=f(x), input x to a category y, for example
* **feedforward** because information flows through from x, through intermediate computations, to the output y
 * no feedback except for rnn
* **network** because represented by composing together many different functions
 * chain structure: f(x) = f3(f2(f1(x)))
* **neural** because it is loosely inspired by neuroscience

#### nonlinearity

* linear model is simle and good, but have limitations
 * ex) XOR problem
* extend to nonlinear model by nonlinear transforming input x to features
* How to choose nonlinear mapping for good features?
 1. generic mapping, ex) kernal machine like RBF.
 2. manually engineer mapping, this is dominant in the field
 3. ** learn mapping **  <-- This is deep-learning approach

#### Design decision

* how to choose optimizer, cost function, form of output unit
 * ex) L2-cost, adam opt, one-hot encoding output
* how to choose activation function
 * ex) sigmoid, tanh, Relu
* how may layers, how many units in each layer, how deep into
* how to connect the units each other
 * ex) fully-connected, drop out, recurrent.

## 6.1 Example: Learning XOR

setup

* as a regression problem and use a mea square error loss function

by linear model

* not able to represent XOR function mapping

by feedforward network with sufficient nonlinearity

* by help of nonlinear function like Relu, input x will be non-linearly transformed into feature space
* in feature space, may linear seperation of transformed inputs are possible (See Figure 6.3)

## 6.2 Gradient-Based Learning

non-convex problem and how to solve

* non-linearity of a network causes loss function to be non-convex
* cannot solve it by linear equation sovlers
* can be solvable by iterative, gradient-based optimizers

what is difficult and how to deal with

* no convergence guarantee, sensitive to initial parameters
* good practices to initialize all weights randomly small values, biases may be zero of small positive values

### 6.2.1 Cost functions


#### typical cost function

* **cross-entropy between traning data and model's predictions**
* with regularized term, like L2

Why log-likelihood is good for the case when output units are saturated

* when output units contains exp term, it may saturate when its argument is very negative/positive
* If then, gradient of that is also saturated --> bad for gradient-based learning
* If used with log-likelihood, log undo exp effect -> no saturated happen -> good for gradient-based learning

### 6.2.2 Output units

## 6.3 Hidden Units

### 6.3.1 Rectified Linear Units and Their Generalizations

### 6.3.2. Logistic Sigmoid and Hyperbolic Tangent

### 6.3.3. Other Hidden Units

## 6.4 Architecture Design

## 6.5 Back-propagation and Other Differentiation Algoritms






















