package fm.strength.worm;

import android.test.ProviderTestCase2;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import fm.strength.testapps.base.TestAppContentProvider;
import fm.strength.testapps.base.TestAppContract;
import fm.strength.testapps.base.TestAppContract.Workouts;

import static fm.strength.worm.Data.Model.Contract;
import static fm.strength.worm.Data.value;
import static org.fest.assertions.api.Assertions.assertThat;

public class BatchTests extends ProviderTestCase2<TestAppContentProvider> {

    @Contract(Workouts.class)
    public static class Workout {
        String name;
    }


    public BatchTests() {
        super(TestAppContentProvider.class, TestAppContract.AUTHORITY);
    }


    public void testSyncBatch() throws Exception {
        Data data = Data.sync(getMockContext());
        data.create(Workouts.CONTENT_URI, value("name", "one"));

        data.batch(TestAppContract.AUTHORITY)
                .destroy(Workouts.CONTENT_URI)
                .create(Workouts.CONTENT_URI, value("name", "two"))
                .create(Workouts.CONTENT_URI, value("name", "three"))
        .execute();

        List<Workout> workouts = data.findAll(Workout.class);

        assertThat(workouts).hasSize(2);
        assertThat(workouts.get(0).name).isEqualTo("two");
        assertThat(workouts.get(1).name).isEqualTo("three");
    }

    public void testAsyncBatch() throws Exception {
        Data data = Data.sync(getMockContext());
        data.create(Workouts.CONTENT_URI, value("name", "one"));

        final CountDownLatch signal = new CountDownLatch(1);

        Data.async(getMockContext())
            .batch(TestAppContract.AUTHORITY)
                .destroy(Workouts.CONTENT_URI)
                .create(Workouts.CONTENT_URI, value("name", "two"))
                .create(Workouts.CONTENT_URI, value("name", "three"))
            .then(new Callback<Boolean>() {
                public void onSuccess(Boolean result) {
                    signal.countDown();
                }
            })
        ;

        assertThat(signal.await(5, TimeUnit.SECONDS)).isTrue();
    }

}
