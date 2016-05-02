import tensorflow as tf
import input_data

# Parameters
learning_rate = 0.01
training_epochs = 25
batch_size = 100
display_step = 1

mnist = input_data.read_data_sets("../MNIST_data/", one_hot=True)

X = tf.placeholder("float", [None, 784])
Y = tf.placeholder("float", [None, 10])

# Create model

# Set model weights
W = tf.Variable(tf.zeros([784,10]))
b = tf.Variable(tf.zeros([10]))

# Construct model
activation = tf.nn.softmax(tf.matmul(X,W) + b) # softmax

# Minimize error using cross entropy
cost = tf.reduce_mean(-tf.reduce_sum(Y*tf.log(activation), reduction_indices=1)) # Cross entropy
optimizer = tf.train.GradientDescentOptimizer(learning_rate).minimize(cost) # Gradient Descent

init = tf.initialize_all_variables()

with tf.Session() as sess:
    sess.run(init)

    for epoch in range(training_epochs):
        avg_cost = 0.
        total_batch = int(mnist.train.num_examples/batch_size)
        # Loop over all batches
        for i in range(total_batch):
            batch_xs, batch_ys = mnist.train.next_batch(batch_size)
            # Fit training using batch data
            sess.run(optimizer,feed_dict={X:batch_xs, Y:batch_ys})
            # Compute avergage loss
            avg_cost += sess.run(cost, feed_dict={X: batch_xs, Y:batch_ys})/total_batch
        # display logs per epoch step
        if epoch % display_step == 0:
            print "Epoch :", "%04d" % (epoch+1), "cost=", "{: .9f}".format(avg_cost)
    print "Optimization Finished"

    # Test model
    correct_prediction = tf.equal(tf.argmax(activation,1), tf.argmax(Y,1))
    # calculate accuracy
    accuracy = tf.reduce_mean(tf.cast(correct_prediction,"float"))
    print "Accuracy:", accuracy.eval({X:mnist.test.images, Y:mnist.test.labels})


