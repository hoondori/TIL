# Introduction

two principles of treating complex system
* modularity & abstraction

## Chain Rule

![](../images/murphy_10/1.png)

* the problem is that it becomes more and more complicated to represent CPD as t gets large
 * O(K^V)

## Conditional Independence

X and Y are conditionally independent given Z
![](../images/murphy_10/2.png)

future is independent of the past given the present
![](../images/murphy_10/3.png)
* called first order markov chain

## Graphical Models

* is a way to represent a joint distribution by making CI assumptions
* lack of edge is CI assumptions
* terminologies
 * Parent, Child, Family, Ancestors, Descendants, Neighbors
 * Topological Ordering
 * Path or Trail
 * Tree, SubGraph, Clique

## Directed graphical Models

* GM whose graph is a DAG
 * known as baysian network, belief network, causal network
* ordered markov property
 * node only depends on its immediate parents, not on all predecessors in topological Ordering

<img src="../images/murphy_10/4.png" width="200">

![](../images/murphy_10/5.png)

# Examples

## Naive Bayes classifiers
![](../images/murphy_10/6.png)
* features are independent
![](../images/murphy_10/7.png)
* tree-augmented naive Bayes
 * see figure 10.2 (b)

## Markov and hidden Markov Models

![](../images/murphy_10/8.png)

* first-order
* second-order
* hidden markov model
  * hidden/observation variables
   * word/waveform
  * transition/observation model
  * estimate hidden state given data

## Medical diagnosis
## Genetic linkage analysis
## Directed Gaussian graphical Models
![](../images/murphy_10/9.png)
* all variables are real-valued
* all the CPDs have gaussian form
* called gaussian bayes net

# Inference

* probabilistic Inference
 * task of estimating unknown quantities from known quantities


* posterior of unknown given knowns
![](../images/murphy_10/10.png)


* query variables by marginalizing out the nuisance variables
 ![](../images/murphy_10/11.png)

# learning

* inference is computing posterior of unknown given knowns
* learning is task of computing MAP estimate of the parameters given data
 ![](../images/murphy_10/12.png)
* In baysian view, inference and learning are same
 * parameters are also node in graph

## plate notation

* data is usually iid
* order of data is meaningless

![](../images/murphy_10/13.png)

![](../images/murphy_10/14.png)

## Learning from complete data

* complete data
 * no missing data, no hidden variables

* factored likelihood with node t
![](../images/murphy_10/15.png)

* factored prior
  ![](../images/murphy_10/16.png)

* factored posterior
 * factored prior plus factored likelihood implies factored posterior
![](../images/murphy_10/17.png)

## learning from missing/latent variables

* likelihood no longer factorized
* no longer convex
* only compute locally optimal ML/MAP
