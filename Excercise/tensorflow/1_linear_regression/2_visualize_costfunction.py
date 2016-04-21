import tensorflow as tf
from matplotlib import pyplot as plt

# tf Graph input
X = [1., 2., 3.]
Y = [1., 2., 3.]
m = n_samples = len(X)

# Set model weights
W = tf.placeholder(tf.float32)

# construct a linear model
hypothesis = tf.mul(X,W)

# cost function
cost = tf.reduce_sum(tf.pow(hypothesis-Y,2))/(m)

# initialize variables
init = tf.initialize_all_variables()

# For graphs
W_val = []
cost_val = []

# Launch the graph
sess = tf.Session()
sess.run(init)
for i in range(-30,50):
    print i*0.1, sess.run(cost, feed_dict={W: i*0.1})
    W_val.append(i*0.1)
    cost_val.append(sess.run(cost, feed_dict={W: i*0.1}))

# Graph display
plt.plot(W_val, cost_val, 'ro')
plt.ylabel('Cost')
plt.xlabel('W')
plt.show()
