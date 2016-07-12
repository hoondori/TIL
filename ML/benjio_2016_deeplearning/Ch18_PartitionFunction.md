# Ch 17. Confronting the partition function


* in undirected graph, many probabilistic models could be unnormalized dist.
* need to be normalized by partition function
* usually, computing partition function is intractable

![](../images/benjio_18/1.png)

![](../images/benjio_18/2.png)

## The log-likelihood gradient

* in gradient descent, decomposed into positive phase, and negative phase

![](../images/benjio_18/3.png)

* positive phase,
 * depending on data and parameters,
 * usually tractable/straightforward
* negative phase
 * partition ft depending on parameters,
 * usually intractable
 * results in expectation of gradient of log-likelihood w.r.t. p(x)
 ![](../images/benjio_18/4.png)
* In energy-based learning,
 * positive phase => push down energy of training examples
  * negative phase => push up on the energy of samples drawn from the model

![](../images/benjio_18/5.png)

## Contrastive Divergence(CD)

* By naive mcmc
 * to estimate expectation of gradient of log-likelihood, sample from models by mcmc
 * Each step of gradient descent, perform mcmc by gibbs sampling
 * to burn-in(or mix-in), sufficient k gibbs steps are required
 * but it is computationally expensive

![](../images/benjio_18/6.png)

* meaning of negative sample
 * draw from model's distribution
 * finding points that the model believes in strongly
 * considered to represent the model's incorrect beliefs about the world
 * hallucinations or fantasy particles

* By Contrastive Divergence
 * main cost of naive mcmc is the cost of burning in the markov chains from a random initialization at each step
 * more clever solution is to initialize markov chains from a distribution that is very close to the model distribution
 * how to? use initial samples from the data distribution(Hinton 2000)

![](../images/benjio_18/7.png)

* CD suffers from spurious model
 * CD fails to suppress spurious mode
 * spurious mode that data dist low, but model dist is high
 * CD initialize mc from data points and then go around in a few steps, it is unlikely to visit spurious modes

 ![](../images/benjio_18/8.png)

 * CD is usually useful for shallow RBM, useful for pretraining shallow models that will later be stacked


* persistent CD(PCD) or SML
 * initialize mc with their states from the previous gradient step
 * vulerable when model dists changes a lot at each of gradient step
