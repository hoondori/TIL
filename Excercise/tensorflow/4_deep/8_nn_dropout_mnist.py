import tensorflow as tf
import input_data

def xavier_init(n_inputs, n_outputs, uniform=True):
    if uniform:
        # 6 was used in this paper
        init_range = tf.sqrt(6.0 / (n_inputs+n_outputs))
        return tf.random_uniform_initializer(-init_range, init_range)
    else:
        # 3 gives us approximately the same limits as above since this repicks
        stddev = tf.sqrt(3.0/(n_inputs+n_outputs))
        return tf.truncated_normal_initializer(stddev=stddev)

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
W1 = tf.get_variable("W1", shape=[784,256], initializer=xavier_init(784,256))
W2 = tf.get_variable("W2", shape=[256,256], initializer=xavier_init(256,256))
W3 = tf.get_variable("W3", shape=[256,128], initializer=xavier_init(256,128))
W4 = tf.get_variable("W4", shape=[128,64], initializer=xavier_init(128,64))
W5 = tf.get_variable("W5", shape=[64,10], initializer=xavier_init(64,10))
B1 = tf.Variable(tf.random_normal([256]), name="Bias1")
B2 = tf.Variable(tf.random_normal([256]), name="Bias2")
B3 = tf.Variable(tf.random_normal([128]), name="Bias3")
B4 = tf.Variable(tf.random_normal([64]), name="Bias4")
B5 = tf.Variable(tf.random_normal([10]), name="Bias5")

# Construct model
dropout_rate = tf.placeholder(tf.float32)
_L1 = tf.nn.relu(tf.add(tf.matmul(X,W1), B1))  # hidden layer with RELU activation
L1 = tf.nn.dropout(_L1,dropout_rate)
_L2 = tf.nn.relu(tf.add(tf.matmul(L1,W2), B2))
L2 = tf.nn.dropout(_L2,dropout_rate)
_L3 = tf.nn.relu(tf.add(tf.matmul(L2,W3), B3))
L3 = tf.nn.dropout(_L3,dropout_rate)
_L4 = tf.nn.relu(tf.add(tf.matmul(L3,W4), B4))
L4 = tf.nn.dropout(_L4,dropout_rate)
hypothesis = tf.add(tf.matmul(L4,W5), B5) # no need to use softmax here


# Minimize error using cross entropy
cost = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(hypothesis,Y)) # softmax loss
optimizer = tf.train.AdamOptimizer(learning_rate=learning_rate).minimize(cost)

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
            sess.run(optimizer,feed_dict={X:batch_xs, Y:batch_ys, dropout_rate:0.7})
            # Compute avergage loss
            avg_cost += sess.run(cost, feed_dict={X: batch_xs, Y:batch_ys, dropout_rate:0.7})/total_batch
        # display logs per epoch step
        if epoch % display_step == 0:
            print "Epoch :", "%04d" % (epoch+1), "cost=", "{: .9f}".format(avg_cost)
    print "Optimization Finished"

    # Test model
    correct_prediction = tf.equal(tf.argmax(hypothesis,1), tf.argmax(Y,1))
    # calculate accuracy
    accuracy = tf.reduce_mean(tf.cast(correct_prediction,"float"))
    print "Accuracy:", accuracy.eval({X:mnist.test.images, Y:mnist.test.labels, dropout_rate: 1.0}) # all participate
