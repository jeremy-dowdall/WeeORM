package fm.strength.worm;

import android.test.ProviderTestCase2;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import fm.strength.testapps.base.TestAppContentProvider;
import fm.strength.testapps.base.TestAppContract;
import fm.strength.testapps.base.TestAppContract.Workouts;
import fm.strength.worm.data.Value;
import fm.strength.worm.util.Err;

import static org.fest.assertions.api.Assertions.assertThat;

import static fm.strength.worm.Data.Model.Contract;
import static fm.strength.worm.ContractContentProvider.ERR_INSERT_FAILED;

public class DataTests extends ProviderTestCase2<TestAppContentProvider> {

    @Contract(Workouts.class)
    public static class Workout {
        String name;
    }


    public DataTests() {
        super(TestAppContentProvider.class, TestAppContract.AUTHORITY);
    }


    public void testCreate() throws Exception {
        try {
            Data.sync(getMockContext()).create(Workouts.CONTENT_URI, new Value[0]);
        } catch(IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo(Err.get(ERR_INSERT_FAILED, Workouts.CONTENT_URI).getMessage());
        }
    }

    public void testFind_async() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        Data.async(getMockContext())
            .find(Workout.class)
            .then(new Callback<Workout>() {
                public void onSuccess(Workout workout) {
                    signal.countDown();
                }
            })
        ;

        assertThat(signal.await(5, TimeUnit.SECONDS)).isTrue();
    }

    public void testFindAll_async() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        Data.async(getMockContext())
            .findAll(Workout.class)
            .then(new Callback<List<Workout>>() {
                public void onSuccess(List<Workout> workouts) {
                    signal.countDown();
                }
            })
        ;

        assertThat(signal.await(5, TimeUnit.SECONDS)).isTrue();
    }

    public void testCreate_async() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        Data.async(getMockContext())
            .create(Workouts.CONTENT_URI)
            .then(new Callback<Long>() {
                public void onFailure(Exception error) {
                    signal.countDown();
                }
            })
        ;

        assertThat(signal.await(5, TimeUnit.SECONDS)).isTrue();
    }

}
