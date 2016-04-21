import tensorflow as tf
import numpy as np

# tf Graph input
# x_data = [[1,1,1,1,1],
#           [0., 2., 0., 4., 0.],
#           [1., 0., 3., 0., 5.]]
# y_data = [1, 2, 3, 4, 5]
xy = np.loadtxt('train.txt', unpack=True, dtype='float32')
x_data = xy[0:-1]
y_data = xy[-1]
print(x_data)
print(y_data)
W = tf.Variable(tf.random_uniform([1,3], -1.0, 1.0))

# hypothesis
hypothesis = tf.matmul(W, x_data)

# cost function
cost = tf.reduce_mean(tf.square(hypothesis-y_data))

# Minimize
a = tf.Variable(0.1) # learning rate
optimizer = tf.train.GradientDescentOptimizer(a)
train = optimizer.minimize(cost)

# initialize variables
init = tf.initialize_all_variables()

# Launch the graph
sess = tf.Session()
sess.run(init)

# Fit the line
for step in xrange(2001):
    sess.run(train)
    if step % 20 == 0:
        print step, sess.run(cost), sess.run(W)
