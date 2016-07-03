
# Energy based learning

[paper](http://yann.lecun.com/exdb/publis/pdf/lecun-06.pdf)


## Energy based model
Capture dependencies by associating a scala energy(a measure of compatibility) to each configuration of the variables

![](./images/energy/1.png)


## Energy based inference
energy function which measure goodness(or badness) of each possible configuration of X, y

![](./images/energy/2.png)

## Uncalibrated energy into probability by Gibbs distribution

![](./images/energy/3.png)

only allowed when
* the integral part converges

impractical when
* intractable when cardinality of y is so High
* or y is a high dimensional so that integral has no analytical solution

## Energy-based training
search for best energy function within a family of energy function indexed by a parameter W

![](./images/energy/4.png)

define loss function and minimize

![](./images/energy/5.png)

### Designing loss functional
must shape the energy surface so that the desired value of Y has lower energy than all the other(undesired) values

![](./images/energy/6.png)

![](./images/energy/7.png)


### Examples of Loss function

#### Energy loss
push down correct one

![](./images/energy/8.png)

#### Generalized Perceptron loss
push down correct one and pull up the others
problem is to become flat energy surface

![](./images/energy/9.png)

#### Generalized Margin loss
create form of margin to create an energy gap between correct answer and the incorrect answers

![](./images/energy/10.png)

![](./images/energy/11.png)

##### Hinge loss
special example of margin loss
![](./images/energy/12.png)

##### Log loss
special example of margin loss
![](./images/energy/13.png)

##### MCE loss
step function or soften logistic function
![](./images/energy/14.png)

##### square-square loss
pin-down the correct energy at zero and pin down the incorrect answer energy above m
![](./images/energy/15.png)

##### square-exponential loss

![](./images/energy/16.png)

#### Negative Log-Likelihood loss

![](./images/energy/17.png)

based on maximum conditional probability principle
