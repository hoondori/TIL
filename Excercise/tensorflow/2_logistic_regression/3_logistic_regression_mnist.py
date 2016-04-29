import tensorflow as tf
import numpy as np
import input_data

def init_weights(shape):
    return tf.Variable(tf.random_normal(shape,stddev=0.01))

# notice we use the same model as linear regression,
# this is because there is a baked in cost function which performs softmax and cross entropy
def model(X,y):
    return tf.matmul(X,w)

mnist = input_data.read_data_sets("../MNIST_data/", one_hot=True)
trX, trY, teX, teY = mnist.train.images, mnist.train.labels, mnist.test.images, mnist.test.labels

X = tf.placeholder("float", [None, 784])
Y = tf.placeholder("float", [None, 10])

# like in linear regression, we need a shared variable weight matrix for logistic regression
w = init_weights([784, 10])

py_x = model(X, w)

cost = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(py_x, Y))
train_op = tf.train.GradientDescentOptimizer(0.05).minimize(cost)
predict_op = tf.argmax(py_x, 1)
print len(trX)
with tf.Session() as sess:
    tf.initialize_all_variables().run()

    for i in range(100):
        for start, end in zip(range(0, len(trX), 128), range(128,len(trX),128)):
            sess.run(train_op, feed_dict={X:trX[start:end], Y: trY[start:end]})
        print i, np.mean(np.argmax(teY, axis=1) == sess.run(predict_op, feed_dict={X:teX, Y:teY}))
